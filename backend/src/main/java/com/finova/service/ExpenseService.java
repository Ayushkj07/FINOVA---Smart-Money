package com.finova.service;

import com.finova.dto.expense.ExpenseRequest;
import com.finova.dto.expense.ExpenseResponse;
import com.finova.dto.expense.ReceiptScanResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    List<ExpenseResponse> getAllExpenses(Long userId);
    List<ExpenseResponse> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    ExpenseResponse getExpenseById(Long userId, Long id);
    ExpenseResponse createExpense(Long userId, ExpenseRequest request);
    ExpenseResponse updateExpense(Long userId, Long id, ExpenseRequest request);
    void deleteExpense(Long userId, Long id);
    ReceiptScanResponse scanReceipt(Long userId, MultipartFile file);
}
