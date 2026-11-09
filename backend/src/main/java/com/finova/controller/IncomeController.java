package com.finova.controller;

import com.finova.dto.income.IncomeRequest;
import com.finova.dto.income.IncomeResponse;
import com.finova.security.UserPrincipal;
import com.finova.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@Tag(name = "Income Management", description = "Track student pocket money, allowances, stipends, and freelance earnings")
public class IncomeController {

    private final IncomeService incomeService;

    @GetMapping
    @Operation(summary = "Get all income transactions for authenticated student")
    public ResponseEntity<List<IncomeResponse>> getAllIncomes(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(incomeService.getIncomesByDateRange(userPrincipal.getId(), startDate, endDate));
        }
        return ResponseEntity.ok(incomeService.getAllIncomes(userPrincipal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get income record by ID")
    public ResponseEntity<IncomeResponse> getIncomeById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(incomeService.getIncomeById(userPrincipal.getId(), id));
    }

    @PostMapping
    @Operation(summary = "Add a new income record")
    public ResponseEntity<IncomeResponse> createIncome(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody IncomeRequest request) {
        return new ResponseEntity<>(incomeService.createIncome(userPrincipal.getId(), request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing income record")
    public ResponseEntity<IncomeResponse> updateIncome(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(incomeService.updateIncome(userPrincipal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an income record")
    public ResponseEntity<Void> deleteIncome(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        incomeService.deleteIncome(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

}
