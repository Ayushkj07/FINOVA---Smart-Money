package com.finova.service;

import com.finova.dto.income.IncomeRequest;
import com.finova.dto.income.IncomeResponse;

import java.time.LocalDate;
import java.util.List;

public interface IncomeService {
    List<IncomeResponse> getAllIncomes(Long userId);
    List<IncomeResponse> getIncomesByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    IncomeResponse getIncomeById(Long userId, Long id);
    IncomeResponse createIncome(Long userId, IncomeRequest request);
    IncomeResponse updateIncome(Long userId, Long id, IncomeRequest request);
    void deleteIncome(Long userId, Long id);
}
