package com.finova.dto.income;

import com.finova.entity.enums.PaymentMethod;
import com.finova.entity.enums.RecurrencePeriod;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class IncomeResponse {


    private Long id;
    private String title;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private PaymentMethod paymentMethod;
    private String description;
    private Boolean isRecurring;
    private RecurrencePeriod recurrencePeriod;
    private Instant createdAt;


    public IncomeResponse() {}

    public IncomeResponse(Long id, String title, BigDecimal amount, String category, LocalDate date, PaymentMethod paymentMethod, String description, Boolean isRecurring, RecurrencePeriod recurrencePeriod, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.isRecurring = isRecurring;
        this.recurrencePeriod = recurrencePeriod;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static IncomeResponseBuilder builder() {
        return new IncomeResponseBuilder();
    }

    public static class IncomeResponseBuilder {
        private Long id;
        private String title;
        private BigDecimal amount;
        private String category;
        private LocalDate date;
        private PaymentMethod paymentMethod;
        private String description;
        private Boolean isRecurring;
        private RecurrencePeriod recurrencePeriod;
        private Instant createdAt;

        public IncomeResponseBuilder() {}

        public IncomeResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public IncomeResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public IncomeResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public IncomeResponseBuilder category(String category) {
            this.category = category;
            return this;
        }

        public IncomeResponseBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public IncomeResponseBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public IncomeResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public IncomeResponseBuilder isRecurring(Boolean isRecurring) {
            this.isRecurring = isRecurring;
            return this;
        }

        public IncomeResponseBuilder recurrencePeriod(RecurrencePeriod recurrencePeriod) {
            this.recurrencePeriod = recurrencePeriod;
            return this;
        }

        public IncomeResponseBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public IncomeResponse build() {
            IncomeResponse instance = new IncomeResponse();
            instance.setId(this.id);
            instance.setTitle(this.title);
            instance.setAmount(this.amount);
            instance.setCategory(this.category);
            instance.setDate(this.date);
            instance.setPaymentMethod(this.paymentMethod);
            instance.setDescription(this.description);
            instance.setIsRecurring(this.isRecurring);
            instance.setRecurrencePeriod(this.recurrencePeriod);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
