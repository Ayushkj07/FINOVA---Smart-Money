package com.finova.entity;

import com.finova.entity.enums.BillingCycle;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class Subscription {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", length = 30)
    private BillingCycle billingCycle = BillingCycle.MONTHLY;

    @Column(length = 50)
    private String category = "ENTERTAINMENT";

    @Column(name = "next_renewal_date", nullable = false)
    private LocalDate nextRenewalDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "reminder_days_before")
    private Integer reminderDaysBefore = 3;

    @Column(length = 2000)
    private String description;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public Subscription() {}

    public Subscription(Long id, User user, String name, BigDecimal amount, BillingCycle billingCycle, String category, LocalDate nextRenewalDate, Boolean isActive, Integer reminderDaysBefore, String description, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.amount = amount;
        this.billingCycle = billingCycle;
        this.category = category;
        this.nextRenewalDate = nextRenewalDate;
        this.isActive = isActive;
        this.reminderDaysBefore = reminderDaysBefore;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static SubscriptionBuilder builder() {
        return new SubscriptionBuilder();
    }

    public static class SubscriptionBuilder {
        private Long id;
        private User user;
        private String name;
        private BigDecimal amount;
        private BillingCycle billingCycle;
        private String category;
        private LocalDate nextRenewalDate;
        private Boolean isActive;
        private Integer reminderDaysBefore;
        private String description;
        private Instant createdAt;
        private Instant updatedAt;

        public SubscriptionBuilder() {}

        public SubscriptionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SubscriptionBuilder user(User user) {
            this.user = user;
            return this;
        }

        public SubscriptionBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SubscriptionBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SubscriptionBuilder billingCycle(BillingCycle billingCycle) {
            this.billingCycle = billingCycle;
            return this;
        }

        public SubscriptionBuilder category(String category) {
            this.category = category;
            return this;
        }

        public SubscriptionBuilder nextRenewalDate(LocalDate nextRenewalDate) {
            this.nextRenewalDate = nextRenewalDate;
            return this;
        }

        public SubscriptionBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public SubscriptionBuilder reminderDaysBefore(Integer reminderDaysBefore) {
            this.reminderDaysBefore = reminderDaysBefore;
            return this;
        }

        public SubscriptionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SubscriptionBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SubscriptionBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Subscription build() {
            Subscription instance = new Subscription();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setName(this.name);
            instance.setAmount(this.amount);
            instance.setBillingCycle(this.billingCycle);
            instance.setCategory(this.category);
            instance.setNextRenewalDate(this.nextRenewalDate);
            instance.setIsActive(this.isActive);
            instance.setReminderDaysBefore(this.reminderDaysBefore);
            instance.setDescription(this.description);
            instance.setCreatedAt(this.createdAt);
            instance.setUpdatedAt(this.updatedAt);
            return instance;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.Instant.now();
    }
}
