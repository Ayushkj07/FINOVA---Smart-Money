package com.finova.dto.expense;

import com.finova.entity.enums.PaymentMethod;
import com.finova.entity.enums.RecurrencePeriod;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class ExpenseResponse {


    private Long id;
    private String title;
    private BigDecimal amount;
    private Long categoryId;
    private String categoryName;
    private LocalDate date;
    private PaymentMethod paymentMethod;
    private String description;
    private String receiptUrl;
    private Boolean isRecurring;
    private RecurrencePeriod recurrencePeriod;
    private Instant createdAt;


    public ExpenseResponse() {}

    public ExpenseResponse(Long id, String title, BigDecimal amount, Long categoryId, String categoryName, LocalDate date, PaymentMethod paymentMethod, String description, String receiptUrl, Boolean isRecurring, RecurrencePeriod recurrencePeriod, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.receiptUrl = receiptUrl;
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

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getReceiptUrl() {
        return this.receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
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

    public static ExpenseResponseBuilder builder() {
        return new ExpenseResponseBuilder();
    }

    public static class ExpenseResponseBuilder {
        private Long id;
        private String title;
        private BigDecimal amount;
        private Long categoryId;
        private String categoryName;
        private LocalDate date;
        private PaymentMethod paymentMethod;
        private String description;
        private String receiptUrl;
        private Boolean isRecurring;
        private RecurrencePeriod recurrencePeriod;
        private Instant createdAt;

        public ExpenseResponseBuilder() {}

        public ExpenseResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExpenseResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExpenseResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseResponseBuilder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public ExpenseResponseBuilder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public ExpenseResponseBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public ExpenseResponseBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public ExpenseResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExpenseResponseBuilder receiptUrl(String receiptUrl) {
            this.receiptUrl = receiptUrl;
            return this;
        }

        public ExpenseResponseBuilder isRecurring(Boolean isRecurring) {
            this.isRecurring = isRecurring;
            return this;
        }

        public ExpenseResponseBuilder recurrencePeriod(RecurrencePeriod recurrencePeriod) {
            this.recurrencePeriod = recurrencePeriod;
            return this;
        }

        public ExpenseResponseBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExpenseResponse build() {
            ExpenseResponse instance = new ExpenseResponse();
            instance.setId(this.id);
            instance.setTitle(this.title);
            instance.setAmount(this.amount);
            instance.setCategoryId(this.categoryId);
            instance.setCategoryName(this.categoryName);
            instance.setDate(this.date);
            instance.setPaymentMethod(this.paymentMethod);
            instance.setDescription(this.description);
            instance.setReceiptUrl(this.receiptUrl);
            instance.setIsRecurring(this.isRecurring);
            instance.setRecurrencePeriod(this.recurrencePeriod);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
