package com.finova.dto.expense;

import com.finova.entity.enums.PaymentMethod;
import com.finova.entity.enums.RecurrencePeriod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {



    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private Long categoryId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private PaymentMethod paymentMethod = PaymentMethod.UPI;

    private String description;

    private String receiptUrl;

    private Boolean isRecurring = false;

    private RecurrencePeriod recurrencePeriod = RecurrencePeriod.NONE;


    public ExpenseRequest() {}

    public ExpenseRequest(String title, BigDecimal amount, String categoryName, Long categoryId, LocalDate date, PaymentMethod paymentMethod, String description, String receiptUrl, Boolean isRecurring, RecurrencePeriod recurrencePeriod) {
        this.title = title;
        this.amount = amount;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.receiptUrl = receiptUrl;
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

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public static ExpenseRequestBuilder builder() {
        return new ExpenseRequestBuilder();
    }

    public static class ExpenseRequestBuilder {
        private String title;
        private BigDecimal amount;
        private String categoryName;
        private Long categoryId;
        private LocalDate date;
        private PaymentMethod paymentMethod;
        private String description;
        private String receiptUrl;
        private Boolean isRecurring;
        private RecurrencePeriod recurrencePeriod;

        public ExpenseRequestBuilder() {}

        public ExpenseRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExpenseRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseRequestBuilder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public ExpenseRequestBuilder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public ExpenseRequestBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public ExpenseRequestBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public ExpenseRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExpenseRequestBuilder receiptUrl(String receiptUrl) {
            this.receiptUrl = receiptUrl;
            return this;
        }

        public ExpenseRequestBuilder isRecurring(Boolean isRecurring) {
            this.isRecurring = isRecurring;
            return this;
        }

        public ExpenseRequestBuilder recurrencePeriod(RecurrencePeriod recurrencePeriod) {
            this.recurrencePeriod = recurrencePeriod;
            return this;
        }

        public ExpenseRequest build() {
            ExpenseRequest instance = new ExpenseRequest();
            instance.setTitle(this.title);
            instance.setAmount(this.amount);
            instance.setCategoryName(this.categoryName);
            instance.setCategoryId(this.categoryId);
            instance.setDate(this.date);
            instance.setPaymentMethod(this.paymentMethod);
            instance.setDescription(this.description);
            instance.setReceiptUrl(this.receiptUrl);
            instance.setIsRecurring(this.isRecurring);
            instance.setRecurrencePeriod(this.recurrencePeriod);
            return instance;
        }
    }
}
