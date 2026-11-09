package com.finova.service;

import com.finova.dto.savings.SavingsContributionRequest;
import com.finova.dto.savings.SavingsGoalRequest;
import com.finova.dto.savings.SavingsGoalResponse;

import java.util.List;

public interface SavingsService {
    List<SavingsGoalResponse> getAllGoals(Long userId);
    SavingsGoalResponse getGoalById(Long userId, Long id);
    SavingsGoalResponse createGoal(Long userId, SavingsGoalRequest request);
    SavingsGoalResponse updateGoal(Long userId, Long id, SavingsGoalRequest request);
    void deleteGoal(Long userId, Long id);
    SavingsGoalResponse addContribution(Long userId, Long goalId, SavingsContributionRequest request);
}
