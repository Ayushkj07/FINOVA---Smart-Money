package com.finova.dto.budget;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class BudgetCategoryDto {


    private Long id;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Limit amount is required")
    @PositiveOrZero(message = "Limit amount must be zero or positive")
    private BigDecimal limitAmount;

    private BigDecimal spentAmount = BigDecimal.ZERO;

    private BigDecimal remainingAmount;

    private Double utilizationPercentage;

    private String alertLevel; // NORMAL, WARNING (70%), HIGH_WARNING (90%), EXCEEDED (100%+)


    public BudgetCategoryDto() {}

    public BudgetCategoryDto(Long id, String categoryName, BigDecimal limitAmount, BigDecimal spentAmount, BigDecimal remainingAmount, Double utilizationPercentage, String alertLevel) {
        this.id = id;
        this.categoryName = categoryName;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.utilizationPercentage = utilizationPercentage;
        this.alertLevel = alertLevel;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getLimitAmount() {
        return this.limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getSpentAmount() {
        return this.spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return this.remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Double getUtilizationPercentage() {
        return this.utilizationPercentage;
    }

    public void setUtilizationPercentage(Double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }

    public String getAlertLevel() {
        return this.alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public static BudgetCategoryDtoBuilder builder() {
        return new BudgetCategoryDtoBuilder();
    }

    public static class BudgetCategoryDtoBuilder {
        private Long id;
        private String categoryName;
        private BigDecimal limitAmount;
        private BigDecimal spentAmount;
        private BigDecimal remainingAmount;
        private Double utilizationPercentage;
        private String alertLevel;

        public BudgetCategoryDtoBuilder() {}

        public BudgetCategoryDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BudgetCategoryDtoBuilder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public BudgetCategoryDtoBuilder limitAmount(BigDecimal limitAmount) {
            this.limitAmount = limitAmount;
            return this;
        }

        public BudgetCategoryDtoBuilder spentAmount(BigDecimal spentAmount) {
            this.spentAmount = spentAmount;
            return this;
        }

        public BudgetCategoryDtoBuilder remainingAmount(BigDecimal remainingAmount) {
            this.remainingAmount = remainingAmount;
            return this;
        }

        public BudgetCategoryDtoBuilder utilizationPercentage(Double utilizationPercentage) {
            this.utilizationPercentage = utilizationPercentage;
            return this;
        }

        public BudgetCategoryDtoBuilder alertLevel(String alertLevel) {
            this.alertLevel = alertLevel;
            return this;
        }

        public BudgetCategoryDto build() {
            BudgetCategoryDto instance = new BudgetCategoryDto();
            instance.setId(this.id);
            instance.setCategoryName(this.categoryName);
            instance.setLimitAmount(this.limitAmount);
            instance.setSpentAmount(this.spentAmount);
            instance.setRemainingAmount(this.remainingAmount);
            instance.setUtilizationPercentage(this.utilizationPercentage);
            instance.setAlertLevel(this.alertLevel);
            return instance;
        }
    }
}
