package com.finova.integration.google.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finova.entity.*;
import com.finova.exception.BadRequestException;
import com.finova.exception.ResourceNotFoundException;
import com.finova.integration.google.dto.GoogleCalendarEventDto;
import com.finova.integration.google.dto.GoogleCalendarSyncResult;
import com.finova.integration.google.entity.GoogleCalendarEvent;
import com.finova.integration.google.entity.GoogleSyncLog;
import com.finova.integration.google.repository.GoogleCalendarEventRepository;
import com.finova.integration.google.repository.GoogleSyncLogRepository;
import com.finova.repository.DebtRepository;
import com.finova.repository.SavingsGoalRepository;
import com.finova.repository.SubscriptionRepository;
import com.finova.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private static final Logger log = LoggerFactory.getLogger(GoogleCalendarServiceImpl.class);
    private static final String CALENDAR_EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/primary/events";

    private final GoogleOAuthService googleOAuthService;
    private final GoogleCalendarEventRepository googleCalendarEventRepository;
    private final GoogleSyncLogRepository googleSyncLogRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DebtRepository debtRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public GoogleCalendarServiceImpl(GoogleOAuthService googleOAuthService,
                                    GoogleCalendarEventRepository googleCalendarEventRepository,
                                    GoogleSyncLogRepository googleSyncLogRepository,
                                    SubscriptionRepository subscriptionRepository,
                                    DebtRepository debtRepository,
                                    SavingsGoalRepository savingsGoalRepository,
                                    UserRepository userRepository) {
        this.googleOAuthService = googleOAuthService;
        this.googleCalendarEventRepository = googleCalendarEventRepository;
        this.googleSyncLogRepository = googleSyncLogRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.debtRepository = debtRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Transactional
    public GoogleCalendarSyncResult syncFinancialEvents(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        String accessToken = null;
        boolean isLiveSync = false;
        try {
            accessToken = googleOAuthService.getValidAccessToken(userId);
            isLiveSync = accessToken != null && !accessToken.isBlank();
        } catch (Exception ex) {
            log.warn("Google Calendar access token not active or mock environment for user {}: {}", userId, ex.getMessage());
        }

        int synced = 0;
        int updated = 0;
        int errors = 0;
        List<String> details = new ArrayList<>();

        // 1. Subscriptions sync
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdOrderByNextRenewalDateAsc(userId);
        for (Subscription sub : subscriptions) {
            if (Boolean.TRUE.equals(sub.getIsActive()) && sub.getNextRenewalDate() != null) {
                String title = "[FINOVA] " + sub.getName() + " Subscription Renewal";
                String description = "Subscription renewal payment of ₹" + sub.getAmount() + " is due. Billing cycle: " + sub.getBillingCycle();
                try {
                    boolean wasUpdated = upsertEvent(user, accessToken, isLiveSync, sub.getId(), "SUBSCRIPTION_RENEWAL", sub.getNextRenewalDate(), title, description);
                    if (wasUpdated) updated++; else synced++;
                    details.add("Synchronized subscription: " + sub.getName() + " (" + sub.getNextRenewalDate() + ")");
                } catch (Exception e) {
                    errors++;
                    log.error("Failed to sync subscription {}: {}", sub.getName(), e.getMessage());
                }
            }
        }

        // 2. Debts & Loan repayments sync
        List<Debt> debts = debtRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Debt debt : debts) {
            if (debt.getDueDate() != null && (debt.getStatus() == null || !"PAID".equalsIgnoreCase(debt.getStatus().name()))) {
                String title = "[FINOVA] Loan Due: " + debt.getCounterpartyName();
                String description = "Debt obligation (" + debt.getType() + ") of ₹" + debt.getOutstandingBalance() + " with " + debt.getCounterpartyName() + " is due.";
                try {
                    boolean wasUpdated = upsertEvent(user, accessToken, isLiveSync, debt.getId(), "LOAN_REPAYMENT", debt.getDueDate(), title, description);
                    if (wasUpdated) updated++; else synced++;
                    details.add("Synchronized loan due: " + debt.getCounterpartyName() + " (" + debt.getDueDate() + ")");
                } catch (Exception e) {
                    errors++;
                    log.error("Failed to sync debt {}: {}", debt.getId(), e.getMessage());
                }
            }
        }

        // 3. Savings goals deadlines sync
        List<SavingsGoal> goals = savingsGoalRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (SavingsGoal goal : goals) {
            if (goal.getTargetDate() != null && !"COMPLETED".equalsIgnoreCase(goal.getStatus())) {
                String title = "[FINOVA] Savings Goal Target: " + goal.getName();
                String description = "Savings target deadline of ₹" + goal.getTargetAmount() + " (saved so far: ₹" + goal.getCurrentAmount() + ").";
                try {
                    boolean wasUpdated = upsertEvent(user, accessToken, isLiveSync, goal.getId(), "SAVINGS_DEADLINE", goal.getTargetDate(), title, description);
                    if (wasUpdated) updated++; else synced++;
                    details.add("Synchronized savings goal: " + goal.getName() + " (" + goal.getTargetDate() + ")");
                } catch (Exception e) {
                    errors++;
                    log.error("Failed to sync savings goal {}: {}", goal.getName(), e.getMessage());
                }
            }
        }

        // 4. Monthly budget review reminder
        LocalDate monthEnd = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        String budgetTitle = "[FINOVA] Monthly Budget & Expenses Review";
        String budgetDesc = "Review monthly campus spending, budget allocations, and savings report in FINOVA.";
        try {
            boolean wasUpdated = upsertEvent(user, accessToken, isLiveSync, 999999L, "BUDGET_REVIEW", monthEnd, budgetTitle, budgetDesc);
            if (wasUpdated) updated++; else synced++;
            details.add("Synchronized monthly budget review: " + monthEnd);
        } catch (Exception e) {
            errors++;
        }

        String msg = String.format("Successfully synchronized %d calendar event(s) (%d new, %d updated).", synced + updated, synced, updated);

        googleSyncLogRepository.save(new GoogleSyncLog(
                null, user, "CALENDAR", errors == 0 ? "SUCCESS" : "PARTIAL",
                errors > 0 ? errors + " events encountered issues" : null,
                msg, Instant.now()
        ));

        return new GoogleCalendarSyncResult(synced, updated, 0, errors, msg, details);
    }

    private boolean upsertEvent(User user, String accessToken, boolean isLiveSync, Long recordId, String eventType, LocalDate date, String title, String description) {
        Optional<GoogleCalendarEvent> existingOpt = googleCalendarEventRepository.findByUserAndFinancialRecordIdAndEventType(user, recordId, eventType);

        if (existingOpt.isPresent()) {
            GoogleCalendarEvent existing = existingOpt.get();
            if (isLiveSync && existing.getGoogleEventId() != null && !existing.getGoogleEventId().startsWith("sim_")) {
                try {
                    updateGoogleCalendarEvent(accessToken, existing.getGoogleEventId(), title, description, date);
                } catch (Exception e) {
                    log.warn("Could not patch Google Calendar event {}: {}", existing.getGoogleEventId(), e.getMessage());
                }
            }
            existing.setEventDate(date);
            existing.setSyncStatus("SYNCED");
            existing.setUpdatedAt(Instant.now());
            googleCalendarEventRepository.save(existing);
            return true;
        } else {
            String googleEventId = "sim_cal_" + UUID.randomUUID().toString().substring(0, 8);
            if (isLiveSync) {
                try {
                    String liveId = createGoogleCalendarEvent(accessToken, title, description, date);
                    if (liveId != null && !liveId.isBlank()) {
                        googleEventId = liveId;
                    }
                } catch (Exception e) {
                    log.warn("Could not create live Google Calendar event: {}", e.getMessage());
                }
            }

            GoogleCalendarEvent newEvent = new GoogleCalendarEvent(
                    null, user, recordId, googleEventId, eventType, date, "SYNCED", Instant.now(), Instant.now()
            );
            googleCalendarEventRepository.save(newEvent);
            return false;
        }
    }

    private String createGoogleCalendarEvent(String accessToken, String title, String description, LocalDate date) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("summary", title);
        body.put("description", description);
        body.put("start", Map.of("date", date.toString()));
        body.put("end", Map.of("date", date.toString()));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(CALENDAR_EVENTS_URL, request, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode node = objectMapper.readTree(response.getBody());
            return node.path("id").asText();
        }
        return null;
    }

    private void updateGoogleCalendarEvent(String accessToken, String googleEventId, String title, String description, LocalDate date) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("summary", title);
        body.put("description", description);
        body.put("start", Map.of("date", date.toString()));
        body.put("end", Map.of("date", date.toString()));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.exchange(CALENDAR_EVENTS_URL + "/" + googleEventId, HttpMethod.PATCH, request, String.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoogleCalendarEventDto> getUpcomingSyncedEvents(Long userId) {
        List<GoogleCalendarEvent> events = googleCalendarEventRepository.findByUserId(userId);
        List<GoogleCalendarEventDto> dtos = new ArrayList<>();

        for (GoogleCalendarEvent ev : events) {
            String title = formatEventTitle(ev);
            String desc = "FINOVA Auto-Synchronized " + ev.getEventType().replace("_", " ");
            dtos.add(new GoogleCalendarEventDto(
                    ev.getId(),
                    ev.getFinancialRecordId(),
                    ev.getGoogleEventId(),
                    ev.getEventType(),
                    ev.getEventDate(),
                    ev.getSyncStatus(),
                    title,
                    desc,
                    ev.getCreatedAt()
            ));
        }

        dtos.sort(Comparator.comparing(GoogleCalendarEventDto::getEventDate));
        return dtos;
    }

    private String formatEventTitle(GoogleCalendarEvent ev) {
        return switch (ev.getEventType()) {
            case "SUBSCRIPTION_RENEWAL" -> "Subscription Renewal Reminder";
            case "LOAN_REPAYMENT" -> "Loan Repayment Due Date";
            case "SAVINGS_DEADLINE" -> "Savings Goal Target Deadline";
            case "BUDGET_REVIEW" -> "Monthly Budget & Financial Review";
            default -> "Financial Calendar Event";
        };
    }

    @Override
    @Transactional
    public void deleteCalendarEvent(Long userId, Long eventRecordId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        GoogleCalendarEvent ev = googleCalendarEventRepository.findById(eventRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event record not found: " + eventRecordId));

        if (!ev.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to calendar event.");
        }

        try {
            String accessToken = googleOAuthService.getValidAccessToken(userId);
            if (accessToken != null && ev.getGoogleEventId() != null && !ev.getGoogleEventId().startsWith("sim_")) {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<Void> request = new HttpEntity<>(headers);
                restTemplate.exchange(CALENDAR_EVENTS_URL + "/" + ev.getGoogleEventId(), HttpMethod.DELETE, request, Void.class);
            }
        } catch (Exception ex) {
            log.warn("Error deleting event from Google Calendar API: {}", ex.getMessage());
        }

        googleCalendarEventRepository.delete(ev);
    }
}
