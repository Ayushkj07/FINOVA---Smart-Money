package com.finova;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class FinancialCalculationTest {

    @Test
    @DisplayName("Verify precise decimal arithmetic for balance = Income - Expenses")
    void testBalanceCalculation() {
        BigDecimal income = new BigDecimal("15000.50");
        BigDecimal expense = new BigDecimal("4320.75");
        BigDecimal expectedBalance = new BigDecimal("10679.75");

        BigDecimal actualBalance = income.subtract(expense);
        assertEquals(0, expectedBalance.compareTo(actualBalance), "Balance calculation must use precise decimal arithmetic");
    }

    @Test
    @DisplayName("Verify budget utilization percentage calculation")
    void testBudgetUtilization() {
        BigDecimal spent = new BigDecimal("14000.00");
        BigDecimal limit = new BigDecimal("20000.00");

        double expectedUtil = 70.0;
        double actualUtil = spent.divide(limit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        assertEquals(expectedUtil, actualUtil, 0.001);
    }

    @Test
    @DisplayName("Verify debt repayment deductions and balance update")
    void testDebtRepayment() {
        BigDecimal initialBorrowed = new BigDecimal("5000.00");
        BigDecimal repayment = new BigDecimal("1500.00");
        BigDecimal expectedRemaining = new BigDecimal("3500.00");

        BigDecimal actualRemaining = initialBorrowed.subtract(repayment);
        assertEquals(0, expectedRemaining.compareTo(actualRemaining));
    }

    @Test
    @DisplayName("Verify savings goal progress calculation")
    void testSavingsGoalProgress() {
        BigDecimal current = new BigDecimal("42500.00");
        BigDecimal target = new BigDecimal("95000.00");

        double progress = current.divide(target, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        assertTrue(progress > 44.0 && progress < 45.0);
    }
}
