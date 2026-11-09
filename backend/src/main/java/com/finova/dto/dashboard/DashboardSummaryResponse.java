package com.finova.dto.dashboard;

import java.math.BigDecimal;

public class DashboardSummaryResponse {


    private BigDecimal totalBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal totalSavings;
    private BigDecimal outstandingDebt;
    private BigDecimal remainingMonthlyBudget;
    private BigDecimal totalMonthlyBudgetLimit;
    private BigDecimal budgetUtilizationPercentage;

    // Period comparisons (percentage change from previous month)
    private Double incomeChangePercentage;
    private Double expenseChangePercentage;
    private Double savingsChangePercentage;
    private Double balanceChangePercentage;

    // Student info
    private String studentName;
    private String currency;


    public DashboardSummaryResponse() {}

    public DashboardSummaryResponse(BigDecimal totalBalance, BigDecimal monthlyIncome, BigDecimal monthlyExpenses, BigDecimal totalSavings, BigDecimal outstandingDebt, BigDecimal remainingMonthlyBudget, BigDecimal totalMonthlyBudgetLimit, BigDecimal budgetUtilizationPercentage, Double incomeChangePercentage, Double expenseChangePercentage, Double savingsChangePercentage, Double balanceChangePercentage, String studentName, String currency) {
        this.totalBalance = totalBalance;
        this.monthlyIncome = monthlyIncome;
        this.monthlyExpenses = monthlyExpenses;
        this.totalSavings = totalSavings;
        this.outstandingDebt = outstandingDebt;
        this.remainingMonthlyBudget = remainingMonthlyBudget;
        this.totalMonthlyBudgetLimit = totalMonthlyBudgetLimit;
        this.budgetUtilizationPercentage = budgetUtilizationPercentage;
        this.incomeChangePercentage = incomeChangePercentage;
        this.expenseChangePercentage = expenseChangePercentage;
        this.savingsChangePercentage = savingsChangePercentage;
        this.balanceChangePercentage = balanceChangePercentage;
        this.studentName = studentName;
        this.currency = currency;
    }

    public BigDecimal getTotalBalance() {
        return this.totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getMonthlyIncome() {
        return this.monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlyExpenses() {
        return this.monthlyExpenses;
    }

    public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public BigDecimal getTotalSavings() {
        return this.totalSavings;
    }

    public void setTotalSavings(BigDecimal totalSavings) {
        this.totalSavings = totalSavings;
    }

    public BigDecimal getOutstandingDebt() {
        return this.outstandingDebt;
    }

    public void setOutstandingDebt(BigDecimal outstandingDebt) {
        this.outstandingDebt = outstandingDebt;
    }

    public BigDecimal getRemainingMonthlyBudget() {
        return this.remainingMonthlyBudget;
    }

    public void setRemainingMonthlyBudget(BigDecimal remainingMonthlyBudget) {
        this.remainingMonthlyBudget = remainingMonthlyBudget;
    }

    public BigDecimal getTotalMonthlyBudgetLimit() {
        return this.totalMonthlyBudgetLimit;
    }

    public void setTotalMonthlyBudgetLimit(BigDecimal totalMonthlyBudgetLimit) {
        this.totalMonthlyBudgetLimit = totalMonthlyBudgetLimit;
    }

    public BigDecimal getBudgetUtilizationPercentage() {
        return this.budgetUtilizationPercentage;
    }

    public void setBudgetUtilizationPercentage(BigDecimal budgetUtilizationPercentage) {
        this.budgetUtilizationPercentage = budgetUtilizationPercentage;
    }

    public Double getIncomeChangePercentage() {
        return this.incomeChangePercentage;
    }

    public void setIncomeChangePercentage(Double incomeChangePercentage) {
        this.incomeChangePercentage = incomeChangePercentage;
    }

    public Double getExpenseChangePercentage() {
        return this.expenseChangePercentage;
    }

    public void setExpenseChangePercentage(Double expenseChangePercentage) {
        this.expenseChangePercentage = expenseChangePercentage;
    }

    public Double getSavingsChangePercentage() {
        return this.savingsChangePercentage;
    }

    public void setSavingsChangePercentage(Double savingsChangePercentage) {
        this.savingsChangePercentage = savingsChangePercentage;
    }

    public Double getBalanceChangePercentage() {
        return this.balanceChangePercentage;
    }

    public void setBalanceChangePercentage(Double balanceChangePercentage) {
        this.balanceChangePercentage = balanceChangePercentage;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static DashboardSummaryResponseBuilder builder() {
        return new DashboardSummaryResponseBuilder();
    }

    public static class DashboardSummaryResponseBuilder {
        private BigDecimal totalBalance;
        private BigDecimal monthlyIncome;
        private BigDecimal monthlyExpenses;
        private BigDecimal totalSavings;
        private BigDecimal outstandingDebt;
        private BigDecimal remainingMonthlyBudget;
        private BigDecimal totalMonthlyBudgetLimit;
        private BigDecimal budgetUtilizationPercentage;
        private Double incomeChangePercentage;
        private Double expenseChangePercentage;
        private Double savingsChangePercentage;
        private Double balanceChangePercentage;
        private String studentName;
        private String currency;

        public DashboardSummaryResponseBuilder() {}

        public DashboardSummaryResponseBuilder totalBalance(BigDecimal totalBalance) {
            this.totalBalance = totalBalance;
            return this;
        }

        public DashboardSummaryResponseBuilder monthlyIncome(BigDecimal monthlyIncome) {
            this.monthlyIncome = monthlyIncome;
            return this;
        }

        public DashboardSummaryResponseBuilder monthlyExpenses(BigDecimal monthlyExpenses) {
            this.monthlyExpenses = monthlyExpenses;
            return this;
        }

        public DashboardSummaryResponseBuilder totalSavings(BigDecimal totalSavings) {
            this.totalSavings = totalSavings;
            return this;
        }

        public DashboardSummaryResponseBuilder outstandingDebt(BigDecimal outstandingDebt) {
            this.outstandingDebt = outstandingDebt;
            return this;
        }

        public DashboardSummaryResponseBuilder remainingMonthlyBudget(BigDecimal remainingMonthlyBudget) {
            this.remainingMonthlyBudget = remainingMonthlyBudget;
            return this;
        }

        public DashboardSummaryResponseBuilder totalMonthlyBudgetLimit(BigDecimal totalMonthlyBudgetLimit) {
            this.totalMonthlyBudgetLimit = totalMonthlyBudgetLimit;
            return this;
        }

        public DashboardSummaryResponseBuilder budgetUtilizationPercentage(BigDecimal budgetUtilizationPercentage) {
            this.budgetUtilizationPercentage = budgetUtilizationPercentage;
            return this;
        }

        public DashboardSummaryResponseBuilder incomeChangePercentage(Double incomeChangePercentage) {
            this.incomeChangePercentage = incomeChangePercentage;
            return this;
        }

        public DashboardSummaryResponseBuilder expenseChangePercentage(Double expenseChangePercentage) {
            this.expenseChangePercentage = expenseChangePercentage;
            return this;
        }

        public DashboardSummaryResponseBuilder savingsChangePercentage(Double savingsChangePercentage) {
            this.savingsChangePercentage = savingsChangePercentage;
            return this;
        }

        public DashboardSummaryResponseBuilder balanceChangePercentage(Double balanceChangePercentage) {
            this.balanceChangePercentage = balanceChangePercentage;
            return this;
        }

        public DashboardSummaryResponseBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public DashboardSummaryResponseBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public DashboardSummaryResponse build() {
            DashboardSummaryResponse instance = new DashboardSummaryResponse();
            instance.setTotalBalance(this.totalBalance);
            instance.setMonthlyIncome(this.monthlyIncome);
            instance.setMonthlyExpenses(this.monthlyExpenses);
            instance.setTotalSavings(this.totalSavings);
            instance.setOutstandingDebt(this.outstandingDebt);
            instance.setRemainingMonthlyBudget(this.remainingMonthlyBudget);
            instance.setTotalMonthlyBudgetLimit(this.totalMonthlyBudgetLimit);
            instance.setBudgetUtilizationPercentage(this.budgetUtilizationPercentage);
            instance.setIncomeChangePercentage(this.incomeChangePercentage);
            instance.setExpenseChangePercentage(this.expenseChangePercentage);
            instance.setSavingsChangePercentage(this.savingsChangePercentage);
            instance.setBalanceChangePercentage(this.balanceChangePercentage);
            instance.setStudentName(this.studentName);
            instance.setCurrency(this.currency);
            return instance;
        }
    }
}
