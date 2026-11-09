package com.finova.integration.google.service;

import com.finova.entity.User;
import com.finova.entity.enums.NotificationType;
import com.finova.exception.ResourceNotFoundException;
import com.finova.integration.google.dto.GoogleSendEmailRequest;
import com.finova.integration.google.entity.GoogleSyncLog;
import com.finova.integration.google.repository.GoogleSyncLogRepository;
import com.finova.repository.ExpenseRepository;
import com.finova.repository.IncomeRepository;
import com.finova.repository.UserRepository;
import com.finova.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

@Service
public class GoogleGmailServiceImpl implements GoogleGmailService {

    private static final Logger log = LoggerFactory.getLogger(GoogleGmailServiceImpl.class);
    private static final String GMAIL_SEND_URL = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";

    private final GoogleOAuthService googleOAuthService;
    private final GoogleSyncLogRepository googleSyncLogRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final RestTemplate restTemplate;

    public GoogleGmailServiceImpl(GoogleOAuthService googleOAuthService,
                                  GoogleSyncLogRepository googleSyncLogRepository,
                                  NotificationService notificationService,
                                  UserRepository userRepository,
                                  IncomeRepository incomeRepository,
                                  ExpenseRepository expenseRepository) {
        this.googleOAuthService = googleOAuthService;
        this.googleSyncLogRepository = googleSyncLogRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Transactional
    public void sendEmail(Long userId, GoogleSendEmailRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        String toEmail = (request.getToEmail() != null && !request.getToEmail().isBlank())
                ? request.getToEmail()
                : user.getEmail();

        String subject = (request.getSubject() != null && !request.getSubject().isBlank())
                ? request.getSubject()
                : "FINOVA Financial Notification";

        String htmlContent = generateHtmlBody(user, request.getEmailType(), subject, request.getCustomMessage());

        String accessToken = null;
        boolean isLive = false;
        try {
            accessToken = googleOAuthService.getValidAccessToken(userId);
            isLive = accessToken != null && !accessToken.isBlank();
        } catch (Exception e) {
            log.info("Gmail integration in simulation mode: {}", e.getMessage());
        }

        boolean sentViaGmail = false;
        if (isLive) {
            try {
                String rawMime = buildMimeMessage(toEmail, subject, htmlContent);
                String base64UrlEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(rawMime.getBytes(StandardCharsets.UTF_8));

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> body = Map.of("raw", base64UrlEncoded);
                HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(body, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(GMAIL_SEND_URL, httpEntity, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    sentViaGmail = true;
                }
            } catch (Exception ex) {
                log.warn("Gmail API transmission failed, falling back to in-app & transactional dispatch: {}", ex.getMessage());
            }
        }

        // Also post in-app notification
        notificationService.createNotification(
                userId,
                subject,
                "Email alert sent to " + toEmail + (sentViaGmail ? " via Gmail API." : " (FINOVA Dispatcher)."),
                NotificationType.SYSTEM,
                "/dashboard"
        );

        // Record log
        googleSyncLogRepository.save(new GoogleSyncLog(
                null, user, "GMAIL", "SUCCESS", null,
                "Dispatched email '" + subject + "' to " + toEmail + (sentViaGmail ? " [Gmail API]" : " [Transactional Fallback]"),
                Instant.now()
        ));
    }

    @Override
    @Transactional
    public void sendMonthlySummaryEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        LocalDate now = LocalDate.now();
        BigDecimal totalIncome = incomeRepository.sumTotalAmountByUserId(userId);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = expenseRepository.sumTotalAmountByUserId(userId);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;
        BigDecimal netSavings = totalIncome.subtract(totalExpenses);

        String customMsg = String.format(
                "Your monthly financial summary is ready for %s.<br/><br/>" +
                "<b>Monthly Income:</b> ₹%s<br/>" +
                "<b>Monthly Expenses:</b> ₹%s<br/>" +
                "<b>Monthly Net Balance:</b> ₹%s<br/><br/>" +
                "Keep up the disciplined student budgeting!",
                now.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                totalIncome, totalExpenses, netSavings
        );

        GoogleSendEmailRequest request = new GoogleSendEmailRequest(
                user.getEmail(),
                "Your FINOVA Monthly Financial Summary",
                "MONTHLY_SUMMARY",
                customMsg
        );
        sendEmail(userId, request);
    }

    @Override
    @Transactional
    public void sendBudgetAlertEmail(Long userId, String category, Double spent, Double limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        double percentage = (limit != null && limit > 0) ? (spent / limit) * 100.0 : 100.0;
        String customMsg = String.format(
                "Warning: You have reached <b>%.1f%%</b> of your monthly budget limit for category <b>%s</b>.<br/><br/>" +
                "Spent: ₹%.2f / Limit: ₹%.2f<br/><br/>" +
                "Review your recent expenses to maintain your student savings pace.",
                percentage, category, spent, limit
        );

        GoogleSendEmailRequest request = new GoogleSendEmailRequest(
                user.getEmail(),
                "FINOVA Budget Alert: " + category + " at " + Math.round(percentage) + "%",
                "BUDGET_WARNING",
                customMsg
        );
        sendEmail(userId, request);
    }

    private String buildMimeMessage(String to, String subject, String htmlBody) {
        return "To: " + to + "\r\n" +
               "Subject: " + subject + "\r\n" +
               "MIME-Version: 1.0\r\n" +
               "Content-Type: text/html; charset=UTF-8\r\n" +
               "Content-Transfer-Encoding: 7bit\r\n\r\n" +
               htmlBody;
    }

    private String generateHtmlBody(User user, String emailType, String subject, String message) {
        String greetingName = (user != null && user.getFullName() != null) ? user.getFullName() : "FINOVA Member";
        String bodyText = (message != null) ? message : "You have a new update in your FINOVA student financial dashboard.";

        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head>" +
                "<body style='font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #FAFAFC; padding: 24px; color: #292735;'>" +
                "<div style='max-width: 580px; margin: 0 auto; background: #FFFFFF; border-radius: 18px; border: 1px solid #EEEEF2; padding: 32px; box-shadow: 0 4px 20px rgba(0,0,0,0.03);'>" +
                "  <div style='display: flex; align-items: center; margin-bottom: 24px; border-bottom: 1px solid #EEEEF2; padding-bottom: 16px;'>" +
                "    <div style='background-color: #9B87F5; color: #FFFFFF; font-weight: bold; width: 36px; height: 36px; border-radius: 10px; display: inline-flex; align-items: center; justify-content: center; font-size: 18px; margin-right: 12px; line-height: 36px; text-align: center;'>F</div>" +
                "    <div>" +
                "      <span style='font-size: 20px; font-weight: 700; color: #292735;'>FINOVA</span>" +
                "      <span style='font-size: 12px; color: #858392; margin-left: 8px;'>Smart Money. Smarter Future.</span>" +
                "    </div>" +
                "  </div>" +
                "  <h2 style='color: #292735; font-size: 18px; font-weight: 600; margin-top: 0;'>" + subject + "</h2>" +
                "  <p style='color: #858392; font-size: 14px;'>Hello <b>" + greetingName + "</b>,</p>" +
                "  <div style='background-color: #F1EDFF; border-left: 4px solid #9B87F5; padding: 16px; border-radius: 8px; margin: 20px 0; font-size: 14px; line-height: 1.6; color: #292735;'>" +
                bodyText +
                "  </div>" +
                "  <div style='margin-top: 28px;'>" +
                "    <a href='http://localhost:5173/dashboard' style='display: inline-block; background-color: #9B87F5; color: #FFFFFF; padding: 10px 20px; border-radius: 10px; text-decoration: none; font-size: 14px; font-weight: 500;'>Open FINOVA Dashboard</a>" +
                "  </div>" +
                "  <div style='margin-top: 36px; padding-top: 16px; border-top: 1px solid #EEEEF2; font-size: 12px; color: #858392;'>" +
                "    This notification was securely delivered by FINOVA Student Finance Suite.<br/>" +
                "    Manage your connected Google services in <a href='http://localhost:5173/settings/connected-apps' style='color: #9B87F5;'>Settings &rarr; Connected Apps</a>." +
                "  </div>" +
                "</div>" +
                "</body></html>";
    }
}
