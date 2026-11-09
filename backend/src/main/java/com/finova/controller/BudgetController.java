package com.finova.controller;

import com.finova.dto.budget.BudgetRequest;
import com.finova.dto.budget.BudgetResponse;
import com.finova.security.UserPrincipal;
import com.finova.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Smart Budget Planner", description = "Set monthly and category budgets with utilization progress and warning tiers")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    @Operation(summary = "Get all student budget plans")
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(budgetService.getAllBudgets(userPrincipal.getId()));
    }

    @GetMapping("/current")
    @Operation(summary = "Get or initialize current month's active budget")
    public ResponseEntity<BudgetResponse> getCurrentBudget(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(budgetService.getCurrentMonthBudget(userPrincipal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudgetById(userPrincipal.getId(), id));
    }

    @PostMapping
    @Operation(summary = "Create or update monthly budget plan")
    public ResponseEntity<BudgetResponse> createOrUpdateBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.createOrUpdateBudget(userPrincipal.getId(), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget plan")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        budgetService.deleteBudget(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

}
