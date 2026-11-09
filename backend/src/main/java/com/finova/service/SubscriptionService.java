package com.finova.service;

import com.finova.dto.subscription.SubscriptionRequest;
import com.finova.dto.subscription.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionResponse> getAllSubscriptions(Long userId);
    List<SubscriptionResponse> getActiveSubscriptions(Long userId);
    SubscriptionResponse getSubscriptionById(Long userId, Long id);
    SubscriptionResponse createSubscription(Long userId, SubscriptionRequest request);
    SubscriptionResponse updateSubscription(Long userId, Long id, SubscriptionRequest request);
    void deleteSubscription(Long userId, Long id);
    SubscriptionResponse toggleStatus(Long userId, Long id);
}
