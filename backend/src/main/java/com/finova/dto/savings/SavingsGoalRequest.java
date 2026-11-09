package com.finova.dto.savings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsGoalRequest {



    @NotBlank(message = "Goal name is required")
    private String name;

    @NotNull(message = "Target amount is required")
    @Positive(message = "Target amount must be positive")
    private BigDecimal targetAmount;

    private BigDecimal currentAmount = BigDecimal.ZERO;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    private String category = "GENERAL";

    private String description;


    public SavingsGoalRequest() {}

    public SavingsGoalRequest(String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate targetDate, String category, String description) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.category = category;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTargetAmount() {
        return this.targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return this.currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getTargetDate() {
        return this.targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static SavingsGoalRequestBuilder builder() {
        return new SavingsGoalRequestBuilder();
    }

    public static class SavingsGoalRequestBuilder {
        private String name;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private LocalDate targetDate;
        private String category;
        private String description;

        public SavingsGoalRequestBuilder() {}

        public SavingsGoalRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SavingsGoalRequestBuilder targetAmount(BigDecimal targetAmount) {
            this.targetAmount = targetAmount;
            return this;
        }

        public SavingsGoalRequestBuilder currentAmount(BigDecimal currentAmount) {
            this.currentAmount = currentAmount;
            return this;
        }

        public SavingsGoalRequestBuilder targetDate(LocalDate targetDate) {
            this.targetDate = targetDate;
            return this;
        }

        public SavingsGoalRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public SavingsGoalRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SavingsGoalRequest build() {
            SavingsGoalRequest instance = new SavingsGoalRequest();
            instance.setName(this.name);
            instance.setTargetAmount(this.targetAmount);
            instance.setCurrentAmount(this.currentAmount);
            instance.setTargetDate(this.targetDate);
            instance.setCategory(this.category);
            instance.setDescription(this.description);
            return instance;
        }
    }
}
