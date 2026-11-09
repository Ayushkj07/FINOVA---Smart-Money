package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_month_year", columnNames = {"user_id", "budget_month", "budget_year"})
})
public class Budget {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "budget_month", nullable = false)
    private Integer month; // 1 to 12

    @Column(name = "budget_year", nullable = false)
    private Integer year;

    @Column(name = "total_budget_limit", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalBudgetLimit;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetCategory> categories = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public Budget() {}

    public Budget(Long id, User user, String name, Integer month, Integer year, BigDecimal totalBudgetLimit, List<BudgetCategory> categories, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.month = month;
        this.year = year;
        this.totalBudgetLimit = totalBudgetLimit;
        this.categories = categories;
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

    public List<BudgetCategory> getCategories() {
        return this.categories;
    }

    public void setCategories(List<BudgetCategory> categories) {
        this.categories = categories;
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

    public static BudgetBuilder builder() {
        return new BudgetBuilder();
    }

    public static class BudgetBuilder {
        private Long id;
        private User user;
        private String name;
        private Integer month;
        private Integer year;
        private BigDecimal totalBudgetLimit;
        private List<BudgetCategory> categories;
        private Instant createdAt;
        private Instant updatedAt;

        public BudgetBuilder() {}

        public BudgetBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BudgetBuilder user(User user) {
            this.user = user;
            return this;
        }

        public BudgetBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BudgetBuilder month(Integer month) {
            this.month = month;
            return this;
        }

        public BudgetBuilder year(Integer year) {
            this.year = year;
            return this;
        }

        public BudgetBuilder totalBudgetLimit(BigDecimal totalBudgetLimit) {
            this.totalBudgetLimit = totalBudgetLimit;
            return this;
        }

        public BudgetBuilder categories(List<BudgetCategory> categories) {
            this.categories = categories;
            return this;
        }

        public BudgetBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BudgetBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Budget build() {
            Budget instance = new Budget();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setName(this.name);
            instance.setMonth(this.month);
            instance.setYear(this.year);
            instance.setTotalBudgetLimit(this.totalBudgetLimit);
            instance.setCategories(this.categories);
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
