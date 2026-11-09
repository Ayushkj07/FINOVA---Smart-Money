package com.finova.service.impl;

import com.finova.dto.dashboard.DashboardAnalyticsResponse;
import com.finova.dto.dashboard.DashboardSummaryResponse;
import com.finova.dto.dashboard.HealthScoreResponse;
import com.finova.entity.Budget;
import com.finova.entity.Expense;
import com.finova.entity.Income;
import com.finova.entity.User;
import com.finova.entity.enums.DebtType;
import com.finova.repository.*;
import com.finova.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(Long userId, String timeFilter) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        LocalDate startOfPrevMonth = startOfMonth.minusMonths(1);
        LocalDate endOfPrevMonth = startOfPrevMonth.withDayOfMonth(startOfPrevMonth.lengthOfMonth());

        // Parse custom or filter range
        LocalDate filterStart = startOfMonth;
        LocalDate filterEnd = endOfMonth;

        if ("TODAY".equalsIgnoreCase(timeFilter)) {
            filterStart = today;
            filterEnd = today;
        } else if ("THIS_WEEK".equalsIgnoreCase(timeFilter)) {
            filterStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
            filterEnd = today;
        } else if ("LAST_MONTH".equalsIgnoreCase(timeFilter)) {
            filterStart = startOfPrevMonth;
            filterEnd = endOfPrevMonth;
        } else if ("LAST_3_MONTHS".equalsIgnoreCase(timeFilter)) {
            filterStart = today.minusMonths(3).withDayOfMonth(1);
            filterEnd = today;
        } else if ("THIS_YEAR".equalsIgnoreCase(timeFilter)) {
            filterStart = today.withDayOfYear(1);
            filterEnd = today;
        }

        // Totals
        BigDecimal totalIncomeAllTime = incomeRepository.sumTotalAmountByUserId(userId);
        BigDecimal totalExpenseAllTime = expenseRepository.sumTotalAmountByUserId(userId);
        BigDecimal totalSavings = savingsGoalRepository.sumTotalSavingsByUserId(userId);
        BigDecimal outstandingDebt = debtRepository.sumTotalBorrowedDebtByUserId(userId);

        BigDecimal currentFilteredIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, filterStart, filterEnd);
        BigDecimal currentFilteredExpense = expenseRepository.sumAmountByUserIdAndDateBetween(userId, filterStart, filterEnd);

        // Previous month comparisons
        BigDecimal prevMonthIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, startOfPrevMonth, endOfPrevMonth);
        BigDecimal prevMonthExpense = expenseRepository.sumAmountByUserIdAndDateBetween(userId, startOfPrevMonth, endOfPrevMonth);

        // Monthly Budget
        Optional<Budget> currentBudgetOpt = budgetRepository.findByUserIdAndMonthAndYear(userId, today.getMonthValue(), today.getYear());
        BigDecimal totalMonthlyBudgetLimit = currentBudgetOpt.map(Budget::getTotalBudgetLimit).orElse(BigDecimal.ZERO);
        BigDecimal thisMonthExpenses = expenseRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal remainingBudget = totalMonthlyBudgetLimit.compareTo(BigDecimal.ZERO) > 0 ? totalMonthlyBudgetLimit.subtract(thisMonthExpenses) : BigDecimal.ZERO;

        // Balance calculation = (All Incomes - All Expenses)
        BigDecimal totalBalance = totalIncomeAllTime.subtract(totalExpenseAllTime);

        // Changes
        Double incomeChange = calculatePercentageChange(prevMonthIncome, currentFilteredIncome);
        Double expenseChange = calculatePercentageChange(prevMonthExpense, currentFilteredExpense);
        BigDecimal prevMonthSavings = prevMonthIncome.subtract(prevMonthExpense).max(BigDecimal.ZERO);
        BigDecimal currentMonthSavings = currentFilteredIncome.subtract(currentFilteredExpense).max(BigDecimal.ZERO);
        Double savingsChange = calculatePercentageChange(prevMonthSavings, currentMonthSavings);

        BigDecimal prevMonthNetBalance = prevMonthIncome.subtract(prevMonthExpense);
        BigDecimal currentNetBalance = currentFilteredIncome.subtract(currentFilteredExpense);
        Double balanceChange = calculatePercentageChange(prevMonthNetBalance, currentNetBalance);

        Double budgetUtilization = 0.0;
        if (totalMonthlyBudgetLimit.compareTo(BigDecimal.ZERO) > 0) {
            budgetUtilization = thisMonthExpenses
                    .divide(totalMonthlyBudgetLimit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        User user = userRepository.findById(userId).orElse(null);
        String studentName = user != null ? user.getFullName() : "Student";
        String currency = (user != null && user.getProfile() != null && user.getProfile().getCurrency() != null)
                ? user.getProfile().getCurrency() : "INR";

        return DashboardSummaryResponse.builder()
                .totalBalance(totalBalance)
                .monthlyIncome(currentFilteredIncome)
                .monthlyExpenses(currentFilteredExpense)
                .totalSavings(totalSavings)
                .outstandingDebt(outstandingDebt)
                .remainingMonthlyBudget(remainingBudget)
                .totalMonthlyBudgetLimit(totalMonthlyBudgetLimit)
                .budgetUtilizationPercentage(BigDecimal.valueOf(budgetUtilization).setScale(1, RoundingMode.HALF_UP))
                .incomeChangePercentage(incomeChange)
                .expenseChangePercentage(expenseChange)
                .savingsChangePercentage(savingsChange)
                .balanceChangePercentage(balanceChange)
                .studentName(studentName)
                .currency(currency)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardAnalyticsResponse getAnalytics(Long userId, String timeFilter) {
        LocalDate today = LocalDate.now();
        List<DashboardAnalyticsResponse.ChartPoint> incomeVsExpense = new ArrayList<>();
        List<DashboardAnalyticsResponse.SpendingBarPoint> monthlySpending = new ArrayList<>();
        List<DashboardAnalyticsResponse.ChartPoint> savingsGrowth = new ArrayList<>();

        // Generate 6 months historical trend
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            LocalDate start = monthDate.withDayOfMonth(1);
            LocalDate end = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
            String monthName = monthDate.format(DateTimeFormatter.ofPattern("MMM"));

            BigDecimal inc = incomeRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
            BigDecimal exp = expenseRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
            BigDecimal sav = inc.subtract(exp).max(BigDecimal.ZERO);

            incomeVsExpense.add(new DashboardAnalyticsResponse.ChartPoint(monthName, inc, exp, sav));
            monthlySpending.add(new DashboardAnalyticsResponse.SpendingBarPoint(monthName, exp));
            savingsGrowth.add(new DashboardAnalyticsResponse.ChartPoint(monthName, inc, exp, sav));
        }

        // Expense Category Pie Breakdown
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        List<Object[]> categoryGroup = expenseRepository.sumAmountGroupByCategoryAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal currentMonthExpenses = expenseRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);

        String[] pastelColors = {"#9B87F5", "#A8C9B5", "#65A982", "#E8B96B", "#DF858B", "#7B64EB", "#4DD0E1", "#BA68C8", "#81C784"};
        List<DashboardAnalyticsResponse.CategoryPieSlice> pieSlices = new ArrayList<>();

        int colorIdx = 0;
        for (Object[] row : categoryGroup) {
            String catName = (String) row[0];
            BigDecimal val = (BigDecimal) row[1];
            double pct = 0.0;
            if (currentMonthExpenses.compareTo(BigDecimal.ZERO) > 0) {
                pct = val.divide(currentMonthExpenses, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            }
            pieSlices.add(new DashboardAnalyticsResponse.CategoryPieSlice(
                    catName,
                    val,
                    pastelColors[colorIdx % pastelColors.length],
                    Math.round(pct * 10.0) / 10.0
            ));
            colorIdx++;
        }

        // Budget Utilization
        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndMonthAndYear(userId, today.getMonthValue(), today.getYear());
        double budgetUtil = 0.0;
        if (budgetOpt.isPresent() && budgetOpt.get().getTotalBudgetLimit().compareTo(BigDecimal.ZERO) > 0) {
            budgetUtil = currentMonthExpenses.divide(budgetOpt.get().getTotalBudgetLimit(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        // Savings Rate
        BigDecimal monthIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        double savingsRate = 0.0;
        if (monthIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal saved = monthIncome.subtract(currentMonthExpenses).max(BigDecimal.ZERO);
            savingsRate = saved.divide(monthIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        return DashboardAnalyticsResponse.builder()
                .incomeVsExpenseTrend(incomeVsExpense)
                .expenseCategoryBreakdown(pieSlices)
                .monthlySpendingTrend(monthlySpending)
                .savingsGrowthTrend(savingsGrowth)
                .budgetUtilization(Math.round(budgetUtil * 10.0) / 10.0)
                .savingsRate(Math.round(savingsRate * 10.0) / 10.0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public HealthScoreResponse getHealthScore(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        BigDecimal monthlyIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal monthlyExpense = expenseRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal totalDebt = debtRepository.sumTotalBorrowedDebtByUserId(userId);
        BigDecimal totalSavings = savingsGoalRepository.sumTotalSavingsByUserId(userId);

        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndMonthAndYear(userId, today.getMonthValue(), today.getYear());

        // 1. Savings Rate Score (0 - 20)
        int savingsScore = 12;
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal saved = monthlyIncome.subtract(monthlyExpense).max(BigDecimal.ZERO);
            double rate = saved.divide(monthlyIncome, 2, RoundingMode.HALF_UP).doubleValue();
            if (rate >= 0.30) savingsScore = 20;
            else if (rate >= 0.20) savingsScore = 17;
            else if (rate >= 0.10) savingsScore = 13;
            else if (rate > 0) savingsScore = 8;
            else savingsScore = 4;
        }

        // 2. Budget Discipline Score (0 - 20)
        int budgetScore = 15;
        if (budgetOpt.isPresent()) {
            BigDecimal limit = budgetOpt.get().getTotalBudgetLimit();
            if (limit.compareTo(BigDecimal.ZERO) > 0) {
                double util = monthlyExpense.divide(limit, 2, RoundingMode.HALF_UP).doubleValue();
                if (util <= 0.70) budgetScore = 20;
                else if (util <= 0.85) budgetScore = 17;
                else if (util <= 1.00) budgetScore = 13;
                else budgetScore = 5; // exceeded
            }
        }

        // 3. Spending Consistency (0 - 20)
        int consistencyScore = 16; // default steady

        // 4. Debt Management (0 - 20)
        int debtScore = 18;
        if (totalDebt.compareTo(BigDecimal.valueOf(10000)) > 0) {
            debtScore = 10;
        } else if (totalDebt.compareTo(BigDecimal.valueOf(5000)) > 0) {
            debtScore = 14;
        }

        // 5. Emergency Savings (0 - 20)
        int emergencyScore = 14;
        if (totalSavings.compareTo(BigDecimal.valueOf(15000)) >= 0) emergencyScore = 20;
        else if (totalSavings.compareTo(BigDecimal.valueOf(5000)) >= 0) emergencyScore = 15;
        else if (totalSavings.compareTo(BigDecimal.ZERO) > 0) emergencyScore = 10;
        else emergencyScore = 5;

        int overall = savingsScore + budgetScore + consistencyScore + debtScore + emergencyScore;
        overall = Math.min(100, Math.max(0, overall));

        String grade = "Good";
        if (overall >= 85) grade = "Excellent";
        else if (overall >= 70) grade = "Good";
        else if (overall >= 50) grade = "Fair";
        else grade = "Needs Attention";

        List<String> suggestions = new ArrayList<>();
        List<String> strengths = new ArrayList<>();

        if (savingsScore >= 15) {
            strengths.add("Strong monthly savings habit (>20% of income retained)");
        } else {
            suggestions.add("Aim to save at least 15% of every allowance or stipend before discretionary spending.");
        }

        if (budgetScore >= 15) {
            strengths.add("Excellent budget discipline, keeping expenses within target limits");
        } else {
            suggestions.add("Keep dining and entertainment spending below 70% of assigned category limits.");
        }

        if (debtScore >= 16) {
            strengths.add("Low peer borrowing debt and minimal liability");
        } else {
            suggestions.add("Prioritize clearing active borrowed balances with friends and family.");
        }

        suggestions.add("Consistently log micro-expenses like quick campus teas and snack runs.");

        return HealthScoreResponse.builder()
                .overallScore(overall)
                .grade(grade)
                .savingsRateScore(savingsScore)
                .budgetDisciplineScore(budgetScore)
                .spendingConsistencyScore(consistencyScore)
                .debtManagementScore(debtScore)
                .emergencySavingsScore(emergencyScore)
                .suggestions(suggestions)
                .strengths(strengths)
                .educationalDisclaimer("This financial health score is for educational and money-management habit tracking purposes only and does not constitute a formal credit rating or financial guarantee.")
                .build();
    }

    private Double calculatePercentageChange(BigDecimal prev, BigDecimal current) {
        if (prev == null || prev.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        if (current == null) current = BigDecimal.ZERO;

        BigDecimal diff = current.subtract(prev);
        return diff.divide(prev, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DashboardServiceImpl.class);

    public DashboardServiceImpl(IncomeRepository incomeRepository, ExpenseRepository expenseRepository, BudgetRepository budgetRepository, SavingsGoalRepository savingsGoalRepository, DebtRepository debtRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
    }

}
