package com.finova.service.impl;

import com.finova.dto.savings.SavingsContributionRequest;
import com.finova.dto.savings.SavingsGoalRequest;
import com.finova.dto.savings.SavingsGoalResponse;
import com.finova.entity.Notification;
import com.finova.entity.SavingsContribution;
import com.finova.entity.SavingsGoal;
import com.finova.entity.User;
import com.finova.entity.enums.NotificationType;
import com.finova.exception.ResourceNotFoundException;
import com.finova.exception.UnauthorizedException;
import com.finova.repository.NotificationRepository;
import com.finova.repository.SavingsContributionRepository;
import com.finova.repository.SavingsGoalRepository;
import com.finova.repository.UserRepository;
import com.finova.service.SavingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsServiceImpl implements SavingsService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final SavingsContributionRepository savingsContributionRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final com.finova.service.RealtimeSyncService realtimeSyncService;

    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoalResponse> getAllGoals(Long userId) {
        return savingsGoalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SavingsGoalResponse getGoalById(Long userId, Long id) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + id));

        if (!goal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this savings goal");
        }

        return mapToResponse(goal);
    }

    @Override
    @Transactional
    public SavingsGoalResponse createGoal(Long userId, SavingsGoalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .name(request.getName().trim())
                .targetAmount(request.getTargetAmount())
                .currentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : BigDecimal.ZERO)
                .targetDate(request.getTargetDate())
                .category(request.getCategory() != null ? request.getCategory() : "GENERAL")
                .description(request.getDescription())
                .status("IN_PROGRESS")
                .build();

        SavingsGoalResponse response = mapToResponse(savingsGoalRepository.save(goal));
        realtimeSyncService.publishEvent(userId, "SAVINGS_UPDATED", response);
        return response;
    }

    @Override
    @Transactional
    public SavingsGoalResponse updateGoal(Long userId, Long id, SavingsGoalRequest request) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + id));

        if (!goal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to edit this savings goal");
        }

        goal.setName(request.getName().trim());
        goal.setTargetAmount(request.getTargetAmount());
        if (request.getCurrentAmount() != null) {
            goal.setCurrentAmount(request.getCurrentAmount());
        }
        goal.setTargetDate(request.getTargetDate());
        if (request.getCategory() != null) goal.setCategory(request.getCategory());
        goal.setDescription(request.getDescription());

        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus("COMPLETED");
        }

        SavingsGoalResponse response = mapToResponse(savingsGoalRepository.save(goal));
        realtimeSyncService.publishEvent(userId, "SAVINGS_UPDATED", response);
        return response;
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long id) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + id));

        if (!goal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this savings goal");
        }

        savingsGoalRepository.delete(goal);
        realtimeSyncService.publishEvent(userId, "SAVINGS_DELETED", java.util.Map.of("id", id));
    }

    @Override
    @Transactional
    public SavingsGoalResponse addContribution(Long userId, Long goalId, SavingsContributionRequest request) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + goalId));

        if (!goal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to contribute to this savings goal");
        }

        SavingsContribution contribution = SavingsContribution.builder()
                .savingsGoal(goal)
                .user(goal.getUser())
                .amount(request.getAmount())
                .contributionDate(request.getContributionDate() != null ? request.getContributionDate() : LocalDate.now())
                .notes(request.getNotes())
                .build();

        savingsContributionRepository.save(contribution);

        goal.setCurrentAmount(goal.getCurrentAmount().add(request.getAmount()));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus("COMPLETED");

            // Create notification for milestone completion
            Notification notification = Notification.builder()
                    .user(goal.getUser())
                    .title("Goal Achieved! \uD83C\uDF89")
                    .message(String.format("Congratulations! You've successfully hit your target of ₹%.2f for '%s'!",
                            goal.getTargetAmount(), goal.getName()))
                    .type(NotificationType.GOAL_MILESTONE)
                    .actionUrl("/savings")
                    .build();
            notificationRepository.save(notification);
        }

        SavingsGoalResponse response = mapToResponse(savingsGoalRepository.save(goal));
        realtimeSyncService.publishEvent(userId, "SAVINGS_UPDATED", response);
        return response;
    }

    private SavingsGoalResponse mapToResponse(SavingsGoal goal) {
        BigDecimal remaining = goal.getTargetAmount().subtract(goal.getCurrentAmount()).max(BigDecimal.ZERO);
        double progress = 0.0;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progress = goal.getCurrentAmount().divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        // Estimate completion date based on pace
        String estDate = goal.getTargetDate().toString();
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate());
        if (daysUntil > 0 && progress > 0) {
            estDate = goal.getTargetDate().toString();
        }

        List<SavingsGoalResponse.ContributionDto> contribDtos = savingsContributionRepository
                .findBySavingsGoalIdOrderByContributionDateDesc(goal.getId())
                .stream()
                .map(c -> SavingsGoalResponse.ContributionDto.builder()
                        .id(c.getId())
                        .amount(c.getAmount())
                        .contributionDate(c.getContributionDate())
                        .notes(c.getNotes())
                        .build())
                .collect(Collectors.toList());

        return SavingsGoalResponse.builder()
                .id(goal.getId())
                .name(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .remainingAmount(remaining)
                .progressPercentage(Math.min(100.0, Math.round(progress * 10.0) / 10.0))
                .targetDate(goal.getTargetDate())
                .estimatedCompletionDate(estDate)
                .category(goal.getCategory())
                .description(goal.getDescription())
                .status(goal.getStatus())
                .contributions(contribDtos)
                .build();
    }

    public SavingsServiceImpl(SavingsGoalRepository savingsGoalRepository, SavingsContributionRepository savingsContributionRepository, NotificationRepository notificationRepository, UserRepository userRepository, com.finova.service.RealtimeSyncService realtimeSyncService) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.savingsContributionRepository = savingsContributionRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.realtimeSyncService = realtimeSyncService;
    }

}
