package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "debt_repayments")
public class DebtRepayment {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debt_id", nullable = false)
    private Debt debt;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public DebtRepayment() {}

    public DebtRepayment(Long id, Debt debt, BigDecimal amount, LocalDate paymentDate, String notes, Instant createdAt) {
        this.id = id;
        this.debt = debt;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Debt getDebt() {
        return this.debt;
    }

    public void setDebt(Debt debt) {
        this.debt = debt;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return this.paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
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

    public static DebtRepaymentBuilder builder() {
        return new DebtRepaymentBuilder();
    }

    public static class DebtRepaymentBuilder {
        private Long id;
        private Debt debt;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String notes;
        private Instant createdAt;

        public DebtRepaymentBuilder() {}

        public DebtRepaymentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DebtRepaymentBuilder debt(Debt debt) {
            this.debt = debt;
            return this;
        }

        public DebtRepaymentBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public DebtRepaymentBuilder paymentDate(LocalDate paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public DebtRepaymentBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public DebtRepaymentBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DebtRepayment build() {
            DebtRepayment instance = new DebtRepayment();
            instance.setId(this.id);
            instance.setDebt(this.debt);
            instance.setAmount(this.amount);
            instance.setPaymentDate(this.paymentDate);
            instance.setNotes(this.notes);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
