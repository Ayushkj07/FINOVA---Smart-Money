package com.finova.controller;

import com.finova.dto.expense.ExpenseRequest;
import com.finova.dto.expense.ExpenseResponse;
import com.finova.dto.expense.ReceiptScanResponse;
import com.finova.security.UserPrincipal;
import com.finova.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expense Management", description = "Track student expenses, daily spending, receipt scans, and category allocations")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    @Operation(summary = "Get all expenses for authenticated student")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(expenseService.getExpensesByDateRange(userPrincipal.getId(), startDate, endDate));
        }
        return ResponseEntity.ok(expenseService.getAllExpenses(userPrincipal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense record by ID")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(userPrincipal.getId(), id));
    }

    @PostMapping
    @Operation(summary = "Record a new expense")
    public ResponseEntity<ExpenseResponse> createExpense(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ExpenseRequest request) {
        return new ResponseEntity<>(expenseService.createExpense(userPrincipal.getId(), request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing expense")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(userPrincipal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense")
    public ResponseEntity<Void> deleteExpense(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        expenseService.deleteExpense(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/scan-receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Scan bill/receipt photograph and extract merchant, date, amount, category")
    public ResponseEntity<ReceiptScanResponse> scanReceipt(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(expenseService.scanReceipt(userPrincipal.getId(), file));
    }

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

}
