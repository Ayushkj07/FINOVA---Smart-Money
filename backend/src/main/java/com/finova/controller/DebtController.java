package com.finova.controller;

import com.finova.dto.debt.DebtRepaymentRequest;
import com.finova.dto.debt.DebtRequest;
import com.finova.dto.debt.DebtResponse;
import com.finova.security.UserPrincipal;
import com.finova.service.DebtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debts")
@Tag(name = "Debt & Loan Management", description = "Track borrowed and lent funds, repayments, balances, and due dates")
public class DebtController {

    private final DebtService debtService;

    @GetMapping
    @Operation(summary = "Get all borrowed and lent records")
    public ResponseEntity<List<DebtResponse>> getAllDebts(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(debtService.getAllDebts(userPrincipal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get debt record by ID")
    public ResponseEntity<DebtResponse> getDebtById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(debtService.getDebtById(userPrincipal.getId(), id));
    }

    @PostMapping
    @Operation(summary = "Record borrowed money or money lent to friends")
    public ResponseEntity<DebtResponse> createDebt(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody DebtRequest request) {
        return new ResponseEntity<>(debtService.createDebt(userPrincipal.getId(), request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update debt details")
    public ResponseEntity<DebtResponse> updateDebt(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody DebtRequest request) {
        return ResponseEntity.ok(debtService.updateDebt(userPrincipal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a debt record")
    public ResponseEntity<Void> deleteDebt(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        debtService.deleteDebt(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/repayments")
    @Operation(summary = "Record a partial or full repayment")
    public ResponseEntity<DebtResponse> addRepayment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody DebtRepaymentRequest request) {
        return ResponseEntity.ok(debtService.addRepayment(userPrincipal.getId(), id, request));
    }

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

}
