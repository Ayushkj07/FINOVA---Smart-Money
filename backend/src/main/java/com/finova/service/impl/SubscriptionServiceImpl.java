package com.finova.service.impl;

import com.finova.dto.subscription.SubscriptionRequest;
import com.finova.dto.subscription.SubscriptionResponse;
import com.finova.entity.Subscription;
import com.finova.entity.User;
import com.finova.exception.ResourceNotFoundException;
import com.finova.exception.UnauthorizedException;
import com.finova.repository.SubscriptionRepository;
import com.finova.repository.UserRepository;
import com.finova.service.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final com.finova.service.RealtimeSyncService realtimeSyncService;

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getAllSubscriptions(Long userId) {
        return subscriptionRepository.findByUserIdOrderByNextRenewalDateAsc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getActiveSubscriptions(Long userId) {
        return subscriptionRepository.findByUserIdAndIsActiveTrueOrderByNextRenewalDateAsc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(Long userId, Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this subscription");
        }

        return mapToResponse(subscription);
    }

    @Override
    @Transactional
    public SubscriptionResponse createSubscription(Long userId, SubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Subscription subscription = Subscription.builder()
                .user(user)
                .name(request.getName().trim())
                .amount(request.getAmount())
                .billingCycle(request.getBillingCycle())
                .category(request.getCategory() != null ? request.getCategory() : "ENTERTAINMENT")
                .nextRenewalDate(request.getNextRenewalDate())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .reminderDaysBefore(request.getReminderDaysBefore() != null ? request.getReminderDaysBefore() : 3)
                .description(request.getDescription())
                .build();

        SubscriptionResponse response = mapToResponse(subscriptionRepository.save(subscription));
        realtimeSyncService.publishEvent(userId, "SUBSCRIPTION_UPDATED", response);
        return response;
    }

    @Override
    @Transactional
    public SubscriptionResponse updateSubscription(Long userId, Long id, SubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to edit this subscription");
        }

        subscription.setName(request.getName().trim());
        subscription.setAmount(request.getAmount());
        subscription.setBillingCycle(request.getBillingCycle());
        if (request.getCategory() != null) subscription.setCategory(request.getCategory());
        subscription.setNextRenewalDate(request.getNextRenewalDate());
        if (request.getIsActive() != null) subscription.setIsActive(request.getIsActive());
        if (request.getReminderDaysBefore() != null) subscription.setReminderDaysBefore(request.getReminderDaysBefore());
        subscription.setDescription(request.getDescription());

        SubscriptionResponse response = mapToResponse(subscriptionRepository.save(subscription));
        realtimeSyncService.publishEvent(userId, "SUBSCRIPTION_UPDATED", response);
        return response;
    }

    @Override
    @Transactional
    public void deleteSubscription(Long userId, Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this subscription");
        }

        subscriptionRepository.delete(subscription);
        realtimeSyncService.publishEvent(userId, "SUBSCRIPTION_DELETED", java.util.Map.of("id", id));
    }

    @Override
    @Transactional
    public SubscriptionResponse toggleStatus(Long userId, Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to modify this subscription");
        }

        subscription.setIsActive(!Boolean.TRUE.equals(subscription.getIsActive()));
        SubscriptionResponse response = mapToResponse(subscriptionRepository.save(subscription));
        realtimeSyncService.publishEvent(userId, "SUBSCRIPTION_UPDATED", response);
        return response;
    }

    private SubscriptionResponse mapToResponse(Subscription s) {
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), s.getNextRenewalDate());

        return SubscriptionResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .amount(s.getAmount())
                .billingCycle(s.getBillingCycle())
                .category(s.getCategory())
                .nextRenewalDate(s.getNextRenewalDate())
                .isActive(s.getIsActive())
                .reminderDaysBefore(s.getReminderDaysBefore())
                .description(s.getDescription())
                .daysUntilRenewal(daysUntil)
                .build();
    }

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository, com.finova.service.RealtimeSyncService realtimeSyncService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.realtimeSyncService = realtimeSyncService;
    }

}
