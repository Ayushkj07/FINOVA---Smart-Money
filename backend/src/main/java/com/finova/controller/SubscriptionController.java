package com.finova.controller;

import com.finova.dto.subscription.SubscriptionRequest;
import com.finova.dto.subscription.SubscriptionResponse;
import com.finova.security.UserPrincipal;
import com.finova.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscription Manager", description = "Manage recurring student subscriptions, renewal dates, and reminders")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    @Operation(summary = "Get all student subscriptions")
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions(userPrincipal.getId()));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active student subscriptions only")
    public ResponseEntity<List<SubscriptionResponse>> getActiveSubscriptions(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscriptions(userPrincipal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(userPrincipal.getId(), id));
    }

    @PostMapping
    @Operation(summary = "Add a new subscription service")
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SubscriptionRequest request) {
        return new ResponseEntity<>(subscriptionService.createSubscription(userPrincipal.getId(), request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscription details")
    public ResponseEntity<SubscriptionResponse> updateSubscription(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.updateSubscription(userPrincipal.getId(), id, request));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle active/paused status of subscription")
    public ResponseEntity<SubscriptionResponse> toggleStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.toggleStatus(userPrincipal.getId(), id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subscription")
    public ResponseEntity<Void> deleteSubscription(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        subscriptionService.deleteSubscription(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

}
