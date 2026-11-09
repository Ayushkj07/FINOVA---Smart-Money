package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "expense_split_members")
public class ExpenseSplitMember {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_split_id", nullable = false)
    private ExpenseSplit expenseSplit;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 150)
    private String email;

    @Column(name = "owed_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal owedAmount;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public ExpenseSplitMember() {}

    public ExpenseSplitMember(Long id, ExpenseSplit expenseSplit, String name, String email, BigDecimal owedAmount, BigDecimal paidAmount, Boolean isPaid, Instant createdAt) {
        this.id = id;
        this.expenseSplit = expenseSplit;
        this.name = name;
        this.email = email;
        this.owedAmount = owedAmount;
        this.paidAmount = paidAmount;
        this.isPaid = isPaid;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExpenseSplit getExpenseSplit() {
        return this.expenseSplit;
    }

    public void setExpenseSplit(ExpenseSplit expenseSplit) {
        this.expenseSplit = expenseSplit;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getOwedAmount() {
        return this.owedAmount;
    }

    public void setOwedAmount(BigDecimal owedAmount) {
        this.owedAmount = owedAmount;
    }

    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Boolean getIsPaid() {
        return this.isPaid;
    }

    public Boolean isIsPaid() {
        return this.isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static ExpenseSplitMemberBuilder builder() {
        return new ExpenseSplitMemberBuilder();
    }

    public static class ExpenseSplitMemberBuilder {
        private Long id;
        private ExpenseSplit expenseSplit;
        private String name;
        private String email;
        private BigDecimal owedAmount;
        private BigDecimal paidAmount;
        private Boolean isPaid;
        private Instant createdAt;

        public ExpenseSplitMemberBuilder() {}

        public ExpenseSplitMemberBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExpenseSplitMemberBuilder expenseSplit(ExpenseSplit expenseSplit) {
            this.expenseSplit = expenseSplit;
            return this;
        }

        public ExpenseSplitMemberBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ExpenseSplitMemberBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ExpenseSplitMemberBuilder owedAmount(BigDecimal owedAmount) {
            this.owedAmount = owedAmount;
            return this;
        }

        public ExpenseSplitMemberBuilder paidAmount(BigDecimal paidAmount) {
            this.paidAmount = paidAmount;
            return this;
        }

        public ExpenseSplitMemberBuilder isPaid(Boolean isPaid) {
            this.isPaid = isPaid;
            return this;
        }

        public ExpenseSplitMemberBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExpenseSplitMember build() {
            ExpenseSplitMember instance = new ExpenseSplitMember();
            instance.setId(this.id);
            instance.setExpenseSplit(this.expenseSplit);
            instance.setName(this.name);
            instance.setEmail(this.email);
            instance.setOwedAmount(this.owedAmount);
            instance.setPaidAmount(this.paidAmount);
            instance.setIsPaid(this.isPaid);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
