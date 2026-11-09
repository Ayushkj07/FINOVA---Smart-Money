package com.finova.service;

import com.finova.dto.budget.BudgetRequest;
import com.finova.dto.budget.BudgetResponse;

import java.util.List;

public interface BudgetService {
    List<BudgetResponse> getAllBudgets(Long userId);
    BudgetResponse getCurrentMonthBudget(Long userId);
    BudgetResponse getBudgetById(Long userId, Long id);
    BudgetResponse createOrUpdateBudget(Long userId, BudgetRequest request);
    void deleteBudget(Long userId, Long id);
}
