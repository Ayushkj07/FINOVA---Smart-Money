package com.finova.service;

import com.finova.dto.debt.DebtRepaymentRequest;
import com.finova.dto.debt.DebtRequest;
import com.finova.dto.debt.DebtResponse;

import java.util.List;

public interface DebtService {
    List<DebtResponse> getAllDebts(Long userId);
    DebtResponse getDebtById(Long userId, Long id);
    DebtResponse createDebt(Long userId, DebtRequest request);
    DebtResponse updateDebt(Long userId, Long id, DebtRequest request);
    void deleteDebt(Long userId, Long id);
    DebtResponse addRepayment(Long userId, Long debtId, DebtRepaymentRequest request);
}
