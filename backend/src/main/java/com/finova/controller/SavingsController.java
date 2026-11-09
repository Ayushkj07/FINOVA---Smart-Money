package com.finova.controller;

import com.finova.dto.savings.SavingsContributionRequest;
import com.finova.dto.savings.SavingsGoalRequest;
import com.finova.dto.savings.SavingsGoalResponse;
import com.finova.security.UserPrincipal;
import com.finova.service.SavingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
@Tag(name = "Savings Goals", description = "Create target funds (Laptop, Trip, Emergency), log micro-deposits, track progress")
public class SavingsController {

    private final SavingsService savingsService;

    @GetMapping
    @Operation(summary = "Get all active and completed savings goals")
    public ResponseEntity<List<SavingsGoalResponse>> getAllGoals(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(savingsService.getAllGoals(userPrincipal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get savings goal by ID")
    public ResponseEntity<SavingsGoalResponse> getGoalById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(savingsService.getGoalById(userPrincipal.getId(), id));
    }

    @PostMapping
    @Operation(summary = "Create a new savings goal")
    public ResponseEntity<SavingsGoalResponse> createGoal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SavingsGoalRequest request) {
        return new ResponseEntity<>(savingsService.createGoal(userPrincipal.getId(), request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing savings goal")
    public ResponseEntity<SavingsGoalResponse> updateGoal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalRequest request) {
        return ResponseEntity.ok(savingsService.updateGoal(userPrincipal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a savings goal")
    public ResponseEntity<Void> deleteGoal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        savingsService.deleteGoal(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/contributions")
    @Operation(summary = "Contribute funds to a savings goal")
    public ResponseEntity<SavingsGoalResponse> addContribution(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody SavingsContributionRequest request) {
        return ResponseEntity.ok(savingsService.addContribution(userPrincipal.getId(), id, request));
    }

    public SavingsController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }

}
