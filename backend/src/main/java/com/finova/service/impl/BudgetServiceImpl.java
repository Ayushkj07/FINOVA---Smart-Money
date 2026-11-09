package com.finova.service.impl;

import com.finova.dto.budget.BudgetCategoryDto;
import com.finova.dto.budget.BudgetRequest;
import com.finova.dto.budget.BudgetResponse;
import com.finova.entity.Budget;
import com.finova.entity.BudgetCategory;
import com.finova.entity.User;
import com.finova.exception.ResourceNotFoundException;
import com.finova.exception.UnauthorizedException;
import com.finova.repository.BudgetCategoryRepository;
import com.finova.repository.BudgetRepository;
import com.finova.repository.ExpenseRepository;
import com.finova.repository.UserRepository;
import com.finova.service.BudgetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final com.finova.service.RealtimeSyncService realtimeSyncService;

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets(Long userId) {
        return budgetRepository.findByUserIdOrderByYearDescMonthDesc(userId)
                .stream()
                .map(b -> enrichBudgetResponse(userId, b))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BudgetResponse getCurrentMonthBudget(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndMonthAndYear(userId, today.getMonthValue(), today.getYear());

        Budget budget = budgetOpt.orElseGet(() -> {
            // Create default starter budget for student
            User user = userRepository.findById(userId).orElseThrow();
            Budget defaultBudget = Budget.builder()
                    .user(user)
                    .name("Monthly Student Budget - " + today.getMonth().name())
                    .month(today.getMonthValue())
                    .year(today.getYear())
                    .totalBudgetLimit(BigDecimal.valueOf(18000.00))
                    .build();
            defaultBudget = budgetRepository.save(defaultBudget);

            // Default categories
            List<BudgetCategory> defaultCats = List.of(
                    BudgetCategory.builder().budget(defaultBudget).categoryName("Food & Groceries").limitAmount(BigDecimal.valueOf(6000.00)).build(),
                    BudgetCategory.builder().budget(defaultBudget).categoryName("Transportation").limitAmount(BigDecimal.valueOf(2500.00)).build(),
                    BudgetCategory.builder().budget(defaultBudget).categoryName("Education & Books").limitAmount(BigDecimal.valueOf(3500.00)).build(),
                    BudgetCategory.builder().budget(defaultBudget).categoryName("Entertainment").limitAmount(BigDecimal.valueOf(3000.00)).build(),
                    BudgetCategory.builder().budget(defaultBudget).categoryName("Subscriptions").limitAmount(BigDecimal.valueOf(1000.00)).build(),
                    BudgetCategory.builder().budget(defaultBudget).categoryName("Other & Miscellaneous").limitAmount(BigDecimal.valueOf(2000.00)).build()
            );
            budgetCategoryRepository.saveAll(defaultCats);
            defaultBudget.setCategories(defaultCats);
            return defaultBudget;
        });

        return enrichBudgetResponse(userId, budget);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long userId, Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        if (!budget.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this budget");
        }

        return enrichBudgetResponse(userId, budget);
    }

    @Override
    @Transactional
    public BudgetResponse createOrUpdateBudget(Long userId, BudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional<Budget> existingOpt = budgetRepository.findByUserIdAndMonthAndYear(userId, request.getMonth(), request.getYear());

        Budget budget;
        if (existingOpt.isPresent()) {
            budget = existingOpt.get();
            budget.setName(request.getName().trim());
            budget.setTotalBudgetLimit(request.getTotalBudgetLimit());
            // Clear existing categories
            budget.getCategories().clear();
        } else {
            budget = Budget.builder()
                    .user(user)
                    .name(request.getName().trim())
                    .month(request.getMonth())
                    .year(request.getYear())
                    .totalBudgetLimit(request.getTotalBudgetLimit())
                    .categories(new ArrayList<>())
                    .build();
        }

        budget = budgetRepository.save(budget);

        if (request.getCategories() != null) {
            for (BudgetCategoryDto catDto : request.getCategories()) {
                BudgetCategory cat = BudgetCategory.builder()
                        .budget(budget)
                        .categoryName(catDto.getCategoryName().trim())
                        .limitAmount(catDto.getLimitAmount())
                        .spentAmount(BigDecimal.ZERO)
                        .build();
                budget.getCategories().add(cat);
            }
            budget = budgetRepository.save(budget);
        }

        BudgetResponse response = enrichBudgetResponse(userId, budget);
        realtimeSyncService.publishEvent(userId, "BUDGET_UPDATED", response);
        return response;
    }

    @Override
    @Transactional
    public void deleteBudget(Long userId, Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        if (!budget.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this budget");
        }

        budgetRepository.delete(budget);
        realtimeSyncService.publishEvent(userId, "BUDGET_DELETED", java.util.Map.of("id", id));
    }

    private BudgetResponse enrichBudgetResponse(Long userId, Budget budget) {
        LocalDate start = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal totalSpent = expenseRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
        BigDecimal remaining = budget.getTotalBudgetLimit().subtract(totalSpent);

        double totalUtil = 0.0;
        if (budget.getTotalBudgetLimit().compareTo(BigDecimal.ZERO) > 0) {
            totalUtil = totalSpent.divide(budget.getTotalBudgetLimit(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        String overallAlert = getAlertLevel(totalUtil);

        List<BudgetCategoryDto> categoryDtos = new ArrayList<>();
        if (budget.getCategories() != null) {
            for (BudgetCategory cat : budget.getCategories()) {
                BigDecimal catSpent = expenseRepository.sumAmountByUserIdAndCategoryAndDateBetween(
                        userId, cat.getCategoryName(), start, end
                );
                BigDecimal catRemaining = cat.getLimitAmount().subtract(catSpent);

                double catUtil = 0.0;
                if (cat.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
                    catUtil = catSpent.divide(cat.getLimitAmount(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue();
                }

                categoryDtos.add(BudgetCategoryDto.builder()
                        .id(cat.getId())
                        .categoryName(cat.getCategoryName())
                        .limitAmount(cat.getLimitAmount())
                        .spentAmount(catSpent)
                        .remainingAmount(catRemaining)
                        .utilizationPercentage(Math.round(catUtil * 10.0) / 10.0)
                        .alertLevel(getAlertLevel(catUtil))
                        .build());
            }
        }

        return BudgetResponse.builder()
                .id(budget.getId())
                .name(budget.getName())
                .month(budget.getMonth())
                .year(budget.getYear())
                .totalBudgetLimit(budget.getTotalBudgetLimit())
                .totalSpentAmount(totalSpent)
                .totalRemainingAmount(remaining)
                .totalUtilizationPercentage(Math.round(totalUtil * 10.0) / 10.0)
                .alertLevel(overallAlert)
                .categories(categoryDtos)
                .build();
    }

    private String getAlertLevel(double utilPct) {
        if (utilPct >= 100.0) return "EXCEEDED";
        if (utilPct >= 90.0) return "HIGH_WARNING";
        if (utilPct >= 70.0) return "WARNING";
        return "NORMAL";
    }

    public BudgetServiceImpl(BudgetRepository budgetRepository, BudgetCategoryRepository budgetCategoryRepository, ExpenseRepository expenseRepository, UserRepository userRepository, com.finova.service.RealtimeSyncService realtimeSyncService) {
        this.budgetRepository = budgetRepository;
        this.budgetCategoryRepository = budgetCategoryRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.realtimeSyncService = realtimeSyncService;
    }

}
