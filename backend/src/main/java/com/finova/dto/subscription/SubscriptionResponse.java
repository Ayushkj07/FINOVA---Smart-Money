package com.finova.dto.subscription;

import com.finova.entity.enums.BillingCycle;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SubscriptionResponse {


    private Long id;
    private String name;
    private BigDecimal amount;
    private BillingCycle billingCycle;
    private String category;
    private LocalDate nextRenewalDate;
    private Boolean isActive;
    private Integer reminderDaysBefore;
    private String description;
    private Long daysUntilRenewal;


    public SubscriptionResponse() {}

    public SubscriptionResponse(Long id, String name, BigDecimal amount, BillingCycle billingCycle, String category, LocalDate nextRenewalDate, Boolean isActive, Integer reminderDaysBefore, String description, Long daysUntilRenewal) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.billingCycle = billingCycle;
        this.category = category;
        this.nextRenewalDate = nextRenewalDate;
        this.isActive = isActive;
        this.reminderDaysBefore = reminderDaysBefore;
        this.description = description;
        this.daysUntilRenewal = daysUntilRenewal;
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

    public Long getDaysUntilRenewal() {
        return this.daysUntilRenewal;
    }

    public void setDaysUntilRenewal(Long daysUntilRenewal) {
        this.daysUntilRenewal = daysUntilRenewal;
    }

    public static SubscriptionResponseBuilder builder() {
        return new SubscriptionResponseBuilder();
    }

    public static class SubscriptionResponseBuilder {
        private Long id;
        private String name;
        private BigDecimal amount;
        private BillingCycle billingCycle;
        private String category;
        private LocalDate nextRenewalDate;
        private Boolean isActive;
        private Integer reminderDaysBefore;
        private String description;
        private Long daysUntilRenewal;

        public SubscriptionResponseBuilder() {}

        public SubscriptionResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SubscriptionResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SubscriptionResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SubscriptionResponseBuilder billingCycle(BillingCycle billingCycle) {
            this.billingCycle = billingCycle;
            return this;
        }

        public SubscriptionResponseBuilder category(String category) {
            this.category = category;
            return this;
        }

        public SubscriptionResponseBuilder nextRenewalDate(LocalDate nextRenewalDate) {
            this.nextRenewalDate = nextRenewalDate;
            return this;
        }

        public SubscriptionResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public SubscriptionResponseBuilder reminderDaysBefore(Integer reminderDaysBefore) {
            this.reminderDaysBefore = reminderDaysBefore;
            return this;
        }

        public SubscriptionResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SubscriptionResponseBuilder daysUntilRenewal(Long daysUntilRenewal) {
            this.daysUntilRenewal = daysUntilRenewal;
            return this;
        }

        public SubscriptionResponse build() {
            SubscriptionResponse instance = new SubscriptionResponse();
            instance.setId(this.id);
            instance.setName(this.name);
            instance.setAmount(this.amount);
            instance.setBillingCycle(this.billingCycle);
            instance.setCategory(this.category);
            instance.setNextRenewalDate(this.nextRenewalDate);
            instance.setIsActive(this.isActive);
            instance.setReminderDaysBefore(this.reminderDaysBefore);
            instance.setDescription(this.description);
            instance.setDaysUntilRenewal(this.daysUntilRenewal);
            return instance;
        }
    }
}
