package com.finova.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public class DashboardAnalyticsResponse {

    public static class ChartPoint {
        private String label;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal savings;

        public ChartPoint() {}
        public ChartPoint(String label, BigDecimal income, BigDecimal expense, BigDecimal savings) {
            this.label = label;
            this.income = income;
            this.expense = expense;
            this.savings = savings;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public BigDecimal getIncome() { return income; }
        public void setIncome(BigDecimal income) { this.income = income; }
        public BigDecimal getExpense() { return expense; }
        public void setExpense(BigDecimal expense) { this.expense = expense; }
        public BigDecimal getSavings() { return savings; }
        public void setSavings(BigDecimal savings) { this.savings = savings; }
    }

    public static class CategoryPieSlice {
        private String name;
        private BigDecimal value;
        private String color;
        private Double percentage;

        public CategoryPieSlice() {}
        public CategoryPieSlice(String name, BigDecimal value, String color, Double percentage) {
            this.name = name;
            this.value = value;
            this.color = color;
            this.percentage = percentage;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
    }

    public static class SpendingBarPoint {
        private String period;
        private BigDecimal amount;

        public SpendingBarPoint() {}
        public SpendingBarPoint(String period, BigDecimal amount) {
            this.period = period;
            this.amount = amount;
        }

        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    private List<ChartPoint> incomeVsExpenseTrend;
    private List<CategoryPieSlice> expenseCategoryBreakdown;
    private List<SpendingBarPoint> monthlySpendingTrend;
    private List<ChartPoint> savingsGrowthTrend;
    private Double budgetUtilization;
    private Double savingsRate;

    public DashboardAnalyticsResponse() {}

    public DashboardAnalyticsResponse(List<ChartPoint> incomeVsExpenseTrend,
                                      List<CategoryPieSlice> expenseCategoryBreakdown,
                                      List<SpendingBarPoint> monthlySpendingTrend,
                                      List<ChartPoint> savingsGrowthTrend,
                                      Double budgetUtilization,
                                      Double savingsRate) {
        this.incomeVsExpenseTrend = incomeVsExpenseTrend;
        this.expenseCategoryBreakdown = expenseCategoryBreakdown;
        this.monthlySpendingTrend = monthlySpendingTrend;
        this.savingsGrowthTrend = savingsGrowthTrend;
        this.budgetUtilization = budgetUtilization;
        this.savingsRate = savingsRate;
    }

    public List<ChartPoint> getIncomeVsExpenseTrend() { return incomeVsExpenseTrend; }
    public void setIncomeVsExpenseTrend(List<ChartPoint> incomeVsExpenseTrend) { this.incomeVsExpenseTrend = incomeVsExpenseTrend; }

    public List<CategoryPieSlice> getExpenseCategoryBreakdown() { return expenseCategoryBreakdown; }
    public void setExpenseCategoryBreakdown(List<CategoryPieSlice> expenseCategoryBreakdown) { this.expenseCategoryBreakdown = expenseCategoryBreakdown; }

    public List<SpendingBarPoint> getMonthlySpendingTrend() { return monthlySpendingTrend; }
    public void setMonthlySpendingTrend(List<SpendingBarPoint> monthlySpendingTrend) { this.monthlySpendingTrend = monthlySpendingTrend; }

    public List<ChartPoint> getSavingsGrowthTrend() { return savingsGrowthTrend; }
    public void setSavingsGrowthTrend(List<ChartPoint> savingsGrowthTrend) { this.savingsGrowthTrend = savingsGrowthTrend; }

    public Double getBudgetUtilization() { return budgetUtilization; }
    public void setBudgetUtilization(Double budgetUtilization) { this.budgetUtilization = budgetUtilization; }

    public Double getSavingsRate() { return savingsRate; }
    public void setSavingsRate(Double savingsRate) { this.savingsRate = savingsRate; }

    public static DashboardAnalyticsResponseBuilder builder() {
        return new DashboardAnalyticsResponseBuilder();
    }

    public static class DashboardAnalyticsResponseBuilder {
        private List<ChartPoint> incomeVsExpenseTrend;
        private List<CategoryPieSlice> expenseCategoryBreakdown;
        private List<SpendingBarPoint> monthlySpendingTrend;
        private List<ChartPoint> savingsGrowthTrend;
        private Double budgetUtilization;
        private Double savingsRate;

        public DashboardAnalyticsResponseBuilder incomeVsExpenseTrend(List<ChartPoint> incomeVsExpenseTrend) {
            this.incomeVsExpenseTrend = incomeVsExpenseTrend;
            return this;
        }

        public DashboardAnalyticsResponseBuilder expenseCategoryBreakdown(List<CategoryPieSlice> expenseCategoryBreakdown) {
            this.expenseCategoryBreakdown = expenseCategoryBreakdown;
            return this;
        }

        public DashboardAnalyticsResponseBuilder monthlySpendingTrend(List<SpendingBarPoint> monthlySpendingTrend) {
            this.monthlySpendingTrend = monthlySpendingTrend;
            return this;
        }

        public DashboardAnalyticsResponseBuilder savingsGrowthTrend(List<ChartPoint> savingsGrowthTrend) {
            this.savingsGrowthTrend = savingsGrowthTrend;
            return this;
        }

        public DashboardAnalyticsResponseBuilder budgetUtilization(Double budgetUtilization) {
            this.budgetUtilization = budgetUtilization;
            return this;
        }

        public DashboardAnalyticsResponseBuilder savingsRate(Double savingsRate) {
            this.savingsRate = savingsRate;
            return this;
        }

        public DashboardAnalyticsResponse build() {
            return new DashboardAnalyticsResponse(incomeVsExpenseTrend, expenseCategoryBreakdown, monthlySpendingTrend, savingsGrowthTrend, budgetUtilization, savingsRate);
        }
    }
}
