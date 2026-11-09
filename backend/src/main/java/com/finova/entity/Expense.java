package com.finova.entity;

import com.finova.entity.enums.PaymentMethod;
import com.finova.entity.enums.RecurrencePeriod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "expenses", indexes = {
    @Index(name = "idx_expense_user_date", columnList = "user_id, date"),
    @Index(name = "idx_expense_category", columnList = "user_id, category_name")
})
public class Expense {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;

    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod = PaymentMethod.UPI;

    @Column(length = 2000)
    private String description;

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_period", length = 30)
    private RecurrencePeriod recurrencePeriod = RecurrencePeriod.NONE;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public Expense() {}

    public Expense(Long id, User user, ExpenseCategory category, String categoryName, String title, BigDecimal amount, LocalDate date, PaymentMethod paymentMethod, String description, String receiptUrl, Boolean isRecurring, RecurrencePeriod recurrencePeriod, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.categoryName = categoryName;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.receiptUrl = receiptUrl;
        this.isRecurring = isRecurring;
        this.recurrencePeriod = recurrencePeriod;
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

    public ExpenseCategory getCategory() {
        return this.category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static ExpenseBuilder builder() {
        return new ExpenseBuilder();
    }

    public static class ExpenseBuilder {
        private Long id;
        private User user;
        private ExpenseCategory category;
        private String categoryName;
        private String title;
        private BigDecimal amount;
        private LocalDate date;
        private PaymentMethod paymentMethod;
        private String description;
        private String receiptUrl;
        private Boolean isRecurring;
        private RecurrencePeriod recurrencePeriod;
        private Instant createdAt;
        private Instant updatedAt;

        public ExpenseBuilder() {}

        public ExpenseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExpenseBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ExpenseBuilder category(ExpenseCategory category) {
            this.category = category;
            return this;
        }

        public ExpenseBuilder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public ExpenseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExpenseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public ExpenseBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public ExpenseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExpenseBuilder receiptUrl(String receiptUrl) {
            this.receiptUrl = receiptUrl;
            return this;
        }

        public ExpenseBuilder isRecurring(Boolean isRecurring) {
            this.isRecurring = isRecurring;
            return this;
        }

        public ExpenseBuilder recurrencePeriod(RecurrencePeriod recurrencePeriod) {
            this.recurrencePeriod = recurrencePeriod;
            return this;
        }

        public ExpenseBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExpenseBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Expense build() {
            Expense instance = new Expense();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setCategory(this.category);
            instance.setCategoryName(this.categoryName);
            instance.setTitle(this.title);
            instance.setAmount(this.amount);
            instance.setDate(this.date);
            instance.setPaymentMethod(this.paymentMethod);
            instance.setDescription(this.description);
            instance.setReceiptUrl(this.receiptUrl);
            instance.setIsRecurring(this.isRecurring);
            instance.setRecurrencePeriod(this.recurrencePeriod);
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
