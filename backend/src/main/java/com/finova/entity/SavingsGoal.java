package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "savings_goals")
public class SavingsGoal {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "target_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", precision = 12, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(length = 50)
    private String category = "GENERAL";

    @Column(length = 2000)
    private String description;

    @Column(length = 30)
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED, PAUSED

    @OneToMany(mappedBy = "savingsGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsContribution> contributions = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public SavingsGoal() {}

    public SavingsGoal(Long id, User user, String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate targetDate, String category, String description, String status, List<SavingsContribution> contributions, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.category = category;
        this.description = description;
        this.status = status;
        this.contributions = contributions;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SavingsContribution> getContributions() {
        return this.contributions;
    }

    public void setContributions(List<SavingsContribution> contributions) {
        this.contributions = contributions;
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

    public static SavingsGoalBuilder builder() {
        return new SavingsGoalBuilder();
    }

    public static class SavingsGoalBuilder {
        private Long id;
        private User user;
        private String name;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private LocalDate targetDate;
        private String category;
        private String description;
        private String status;
        private List<SavingsContribution> contributions;
        private Instant createdAt;
        private Instant updatedAt;

        public SavingsGoalBuilder() {}

        public SavingsGoalBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SavingsGoalBuilder user(User user) {
            this.user = user;
            return this;
        }

        public SavingsGoalBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SavingsGoalBuilder targetAmount(BigDecimal targetAmount) {
            this.targetAmount = targetAmount;
            return this;
        }

        public SavingsGoalBuilder currentAmount(BigDecimal currentAmount) {
            this.currentAmount = currentAmount;
            return this;
        }

        public SavingsGoalBuilder targetDate(LocalDate targetDate) {
            this.targetDate = targetDate;
            return this;
        }

        public SavingsGoalBuilder category(String category) {
            this.category = category;
            return this;
        }

        public SavingsGoalBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SavingsGoalBuilder status(String status) {
            this.status = status;
            return this;
        }

        public SavingsGoalBuilder contributions(List<SavingsContribution> contributions) {
            this.contributions = contributions;
            return this;
        }

        public SavingsGoalBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SavingsGoalBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public SavingsGoal build() {
            SavingsGoal instance = new SavingsGoal();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setName(this.name);
            instance.setTargetAmount(this.targetAmount);
            instance.setCurrentAmount(this.currentAmount);
            instance.setTargetDate(this.targetDate);
            instance.setCategory(this.category);
            instance.setDescription(this.description);
            instance.setStatus(this.status);
            instance.setContributions(this.contributions);
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
