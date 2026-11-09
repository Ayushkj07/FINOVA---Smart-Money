package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "budget_categories")
public class BudgetCategory {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

    @Column(name = "limit_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "spent_amount", precision = 12, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public BudgetCategory() {}

    public BudgetCategory(Long id, Budget budget, String categoryName, BigDecimal limitAmount, BigDecimal spentAmount, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.budget = budget;
        this.categoryName = categoryName;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Budget getBudget() {
        return this.budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
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

    public static BudgetCategoryBuilder builder() {
        return new BudgetCategoryBuilder();
    }

    public static class BudgetCategoryBuilder {
        private Long id;
        private Budget budget;
        private String categoryName;
        private BigDecimal limitAmount;
        private BigDecimal spentAmount;
        private Instant createdAt;
        private Instant updatedAt;

        public BudgetCategoryBuilder() {}

        public BudgetCategoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BudgetCategoryBuilder budget(Budget budget) {
            this.budget = budget;
            return this;
        }

        public BudgetCategoryBuilder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public BudgetCategoryBuilder limitAmount(BigDecimal limitAmount) {
            this.limitAmount = limitAmount;
            return this;
        }

        public BudgetCategoryBuilder spentAmount(BigDecimal spentAmount) {
            this.spentAmount = spentAmount;
            return this;
        }

        public BudgetCategoryBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BudgetCategoryBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BudgetCategory build() {
            BudgetCategory instance = new BudgetCategory();
            instance.setId(this.id);
            instance.setBudget(this.budget);
            instance.setCategoryName(this.categoryName);
            instance.setLimitAmount(this.limitAmount);
            instance.setSpentAmount(this.spentAmount);
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
