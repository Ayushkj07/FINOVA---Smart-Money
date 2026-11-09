package com.finova.dto.subscription;

import com.finova.entity.enums.BillingCycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SubscriptionRequest {



    @NotBlank(message = "Subscription name is required")
    private String name;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private BillingCycle billingCycle = BillingCycle.MONTHLY;

    private String category = "ENTERTAINMENT";

    @NotNull(message = "Next renewal date is required")
    private LocalDate nextRenewalDate;

    private Boolean isActive = true;

    private Integer reminderDaysBefore = 3;

    private String description;


    public SubscriptionRequest() {}

    public SubscriptionRequest(String name, BigDecimal amount, BillingCycle billingCycle, String category, LocalDate nextRenewalDate, Boolean isActive, Integer reminderDaysBefore, String description) {
        this.name = name;
        this.amount = amount;
        this.billingCycle = billingCycle;
        this.category = category;
        this.nextRenewalDate = nextRenewalDate;
        this.isActive = isActive;
        this.reminderDaysBefore = reminderDaysBefore;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BillingCycle getBillingCycle() {
        return this.billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getNextRenewalDate() {
        return this.nextRenewalDate;
    }

    public void setNextRenewalDate(LocalDate nextRenewalDate) {
        this.nextRenewalDate = nextRenewalDate;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Boolean isIsActive() {
        return this.isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getReminderDaysBefore() {
        return this.reminderDaysBefore;
    }

    public void setReminderDaysBefore(Integer reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static SubscriptionRequestBuilder builder() {
        return new SubscriptionRequestBuilder();
    }

    public static class SubscriptionRequestBuilder {
        private String name;
        private BigDecimal amount;
        private BillingCycle billingCycle;
        private String category;
        private LocalDate nextRenewalDate;
        private Boolean isActive;
        private Integer reminderDaysBefore;
        private String description;

        public SubscriptionRequestBuilder() {}

        public SubscriptionRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SubscriptionRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SubscriptionRequestBuilder billingCycle(BillingCycle billingCycle) {
            this.billingCycle = billingCycle;
            return this;
        }

        public SubscriptionRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public SubscriptionRequestBuilder nextRenewalDate(LocalDate nextRenewalDate) {
            this.nextRenewalDate = nextRenewalDate;
            return this;
        }

        public SubscriptionRequestBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public SubscriptionRequestBuilder reminderDaysBefore(Integer reminderDaysBefore) {
            this.reminderDaysBefore = reminderDaysBefore;
            return this;
        }

        public SubscriptionRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SubscriptionRequest build() {
            SubscriptionRequest instance = new SubscriptionRequest();
            instance.setName(this.name);
            instance.setAmount(this.amount);
            instance.setBillingCycle(this.billingCycle);
            instance.setCategory(this.category);
            instance.setNextRenewalDate(this.nextRenewalDate);
            instance.setIsActive(this.isActive);
            instance.setReminderDaysBefore(this.reminderDaysBefore);
            instance.setDescription(this.description);
            return instance;
        }
    }
}
