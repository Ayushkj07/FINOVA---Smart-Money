package com.finova.dto.budget;

import java.math.BigDecimal;
import java.util.List;

public class BudgetResponse {


    private Long id;
    private String name;
    private Integer month;
    private Integer year;
    private BigDecimal totalBudgetLimit;
    private BigDecimal totalSpentAmount;
    private BigDecimal totalRemainingAmount;
    private Double totalUtilizationPercentage;
    private String alertLevel; // NORMAL, WARNING, HIGH_WARNING, EXCEEDED
    private List<BudgetCategoryDto> categories;


    public BudgetResponse() {}

    public BudgetResponse(Long id, String name, Integer month, Integer year, BigDecimal totalBudgetLimit, BigDecimal totalSpentAmount, BigDecimal totalRemainingAmount, Double totalUtilizationPercentage, String alertLevel, List<BudgetCategoryDto> categories) {
        this.id = id;
        this.name = name;
        this.month = month;
        this.year = year;
        this.totalBudgetLimit = totalBudgetLimit;
        this.totalSpentAmount = totalSpentAmount;
        this.totalRemainingAmount = totalRemainingAmount;
        this.totalUtilizationPercentage = totalUtilizationPercentage;
        this.alertLevel = alertLevel;
        this.categories = categories;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMonth() {
        return this.month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getTotalBudgetLimit() {
        return this.totalBudgetLimit;
    }

    public void setTotalBudgetLimit(BigDecimal totalBudgetLimit) {
        this.totalBudgetLimit = totalBudgetLimit;
    }

    public BigDecimal getTotalSpentAmount() {
        return this.totalSpentAmount;
    }

    public void setTotalSpentAmount(BigDecimal totalSpentAmount) {
        this.totalSpentAmount = totalSpentAmount;
    }

    public BigDecimal getTotalRemainingAmount() {
        return this.totalRemainingAmount;
    }

    public void setTotalRemainingAmount(BigDecimal totalRemainingAmount) {
        this.totalRemainingAmount = totalRemainingAmount;
    }

    public Double getTotalUtilizationPercentage() {
        return this.totalUtilizationPercentage;
    }

    public void setTotalUtilizationPercentage(Double totalUtilizationPercentage) {
        this.totalUtilizationPercentage = totalUtilizationPercentage;
    }

    public String getAlertLevel() {
        return this.alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public List<BudgetCategoryDto> getCategories() {
        return this.categories;
    }

    public void setCategories(List<BudgetCategoryDto> categories) {
        this.categories = categories;
    }

    public static BudgetResponseBuilder builder() {
        return new BudgetResponseBuilder();
    }

    public static class BudgetResponseBuilder {
        private Long id;
        private String name;
        private Integer month;
        private Integer year;
        private BigDecimal totalBudgetLimit;
        private BigDecimal totalSpentAmount;
        private BigDecimal totalRemainingAmount;
        private Double totalUtilizationPercentage;
        private String alertLevel;
        private List<BudgetCategoryDto> categories;

        public BudgetResponseBuilder() {}

        public BudgetResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BudgetResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BudgetResponseBuilder month(Integer month) {
            this.month = month;
            return this;
        }

        public BudgetResponseBuilder year(Integer year) {
            this.year = year;
            return this;
        }

        public BudgetResponseBuilder totalBudgetLimit(BigDecimal totalBudgetLimit) {
            this.totalBudgetLimit = totalBudgetLimit;
            return this;
        }

        public BudgetResponseBuilder totalSpentAmount(BigDecimal totalSpentAmount) {
            this.totalSpentAmount = totalSpentAmount;
            return this;
        }

        public BudgetResponseBuilder totalRemainingAmount(BigDecimal totalRemainingAmount) {
            this.totalRemainingAmount = totalRemainingAmount;
            return this;
        }

        public BudgetResponseBuilder totalUtilizationPercentage(Double totalUtilizationPercentage) {
            this.totalUtilizationPercentage = totalUtilizationPercentage;
            return this;
        }

        public BudgetResponseBuilder alertLevel(String alertLevel) {
            this.alertLevel = alertLevel;
            return this;
        }

        public BudgetResponseBuilder categories(List<BudgetCategoryDto> categories) {
            this.categories = categories;
            return this;
        }

        public BudgetResponse build() {
            BudgetResponse instance = new BudgetResponse();
            instance.setId(this.id);
            instance.setName(this.name);
            instance.setMonth(this.month);
            instance.setYear(this.year);
            instance.setTotalBudgetLimit(this.totalBudgetLimit);
            instance.setTotalSpentAmount(this.totalSpentAmount);
            instance.setTotalRemainingAmount(this.totalRemainingAmount);
            instance.setTotalUtilizationPercentage(this.totalUtilizationPercentage);
            instance.setAlertLevel(this.alertLevel);
            instance.setCategories(this.categories);
            return instance;
        }
    }
}
