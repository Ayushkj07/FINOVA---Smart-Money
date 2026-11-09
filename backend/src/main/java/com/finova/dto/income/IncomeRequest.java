package com.finova.dto.income;

import com.finova.entity.enums.PaymentMethod;
import com.finova.entity.enums.RecurrencePeriod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeRequest {



    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;

    private String description;

    private Boolean isRecurring = false;

    private RecurrencePeriod recurrencePeriod = RecurrencePeriod.NONE;


    public IncomeRequest() {}

    public IncomeRequest(String title, BigDecimal amount, String category, LocalDate date, PaymentMethod paymentMethod, String description, Boolean isRecurring, RecurrencePeriod recurrencePeriod) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.isRecurring = isRecurring;
        this.recurrencePeriod = recurrencePeriod;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsRecurring() {
        return this.isRecurring;
    }

    public Boolean isIsRecurring() {
        return this.isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public RecurrencePeriod getRecurrencePeriod() {
        return this.recurrencePeriod;
    }

    public void setRecurrencePeriod(RecurrencePeriod recurrencePeriod) {
        this.recurrencePeriod = recurrencePeriod;
    }

    public static IncomeRequestBuilder builder() {
        return new IncomeRequestBuilder();
    }

    public static class IncomeRequestBuilder {
        private String title;
        private BigDecimal amount;
        private String category;
        private LocalDate date;
        private PaymentMethod paymentMethod;
        private String description;
        private Boolean isRecurring;
        private RecurrencePeriod recurrencePeriod;

        public IncomeRequestBuilder() {}

        public IncomeRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public IncomeRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public IncomeRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public IncomeRequestBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public IncomeRequestBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public IncomeRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public IncomeRequestBuilder isRecurring(Boolean isRecurring) {
            this.isRecurring = isRecurring;
            return this;
        }

        public IncomeRequestBuilder recurrencePeriod(RecurrencePeriod recurrencePeriod) {
            this.recurrencePeriod = recurrencePeriod;
            return this;
        }

        public IncomeRequest build() {
            IncomeRequest instance = new IncomeRequest();
            instance.setTitle(this.title);
            instance.setAmount(this.amount);
            instance.setCategory(this.category);
            instance.setDate(this.date);
            instance.setPaymentMethod(this.paymentMethod);
            instance.setDescription(this.description);
            instance.setIsRecurring(this.isRecurring);
            instance.setRecurrencePeriod(this.recurrencePeriod);
            return instance;
        }
    }
}
