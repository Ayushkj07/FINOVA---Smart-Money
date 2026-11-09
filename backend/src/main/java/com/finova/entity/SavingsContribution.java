package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "savings_contributions")
public class SavingsContribution {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_goal_id", nullable = false)
    private SavingsGoal savingsGoal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "contribution_date", nullable = false)
    private LocalDate contributionDate;

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public SavingsContribution() {}

    public SavingsContribution(Long id, SavingsGoal savingsGoal, User user, BigDecimal amount, LocalDate contributionDate, String notes, Instant createdAt) {
        this.id = id;
        this.savingsGoal = savingsGoal;
        this.user = user;
        this.amount = amount;
        this.contributionDate = contributionDate;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SavingsGoal getSavingsGoal() {
        return this.savingsGoal;
    }

    public void setSavingsGoal(SavingsGoal savingsGoal) {
        this.savingsGoal = savingsGoal;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getContributionDate() {
        return this.contributionDate;
    }

    public void setContributionDate(LocalDate contributionDate) {
        this.contributionDate = contributionDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static SavingsContributionBuilder builder() {
        return new SavingsContributionBuilder();
    }

    public static class SavingsContributionBuilder {
        private Long id;
        private SavingsGoal savingsGoal;
        private User user;
        private BigDecimal amount;
        private LocalDate contributionDate;
        private String notes;
        private Instant createdAt;

        public SavingsContributionBuilder() {}

        public SavingsContributionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SavingsContributionBuilder savingsGoal(SavingsGoal savingsGoal) {
            this.savingsGoal = savingsGoal;
            return this;
        }

        public SavingsContributionBuilder user(User user) {
            this.user = user;
            return this;
        }

        public SavingsContributionBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SavingsContributionBuilder contributionDate(LocalDate contributionDate) {
            this.contributionDate = contributionDate;
            return this;
        }

        public SavingsContributionBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public SavingsContributionBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SavingsContribution build() {
            SavingsContribution instance = new SavingsContribution();
            instance.setId(this.id);
            instance.setSavingsGoal(this.savingsGoal);
            instance.setUser(this.user);
            instance.setAmount(this.amount);
            instance.setContributionDate(this.contributionDate);
            instance.setNotes(this.notes);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
