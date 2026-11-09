package com.finova.dto.debt;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DebtRepaymentRequest {



    @NotNull(message = "Repayment amount is required")
    @Positive(message = "Repayment amount must be positive")
    private BigDecimal amount;

    private LocalDate paymentDate = LocalDate.now();

    private String notes;


    public DebtRepaymentRequest() {}

    public DebtRepaymentRequest(BigDecimal amount, LocalDate paymentDate, String notes) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.notes = notes;
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

    public static DebtRepaymentRequestBuilder builder() {
        return new DebtRepaymentRequestBuilder();
    }

    public static class DebtRepaymentRequestBuilder {
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String notes;

        public DebtRepaymentRequestBuilder() {}

        public DebtRepaymentRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public DebtRepaymentRequestBuilder paymentDate(LocalDate paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public DebtRepaymentRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public DebtRepaymentRequest build() {
            DebtRepaymentRequest instance = new DebtRepaymentRequest();
            instance.setAmount(this.amount);
            instance.setPaymentDate(this.paymentDate);
            instance.setNotes(this.notes);
            return instance;
        }
    }
}
