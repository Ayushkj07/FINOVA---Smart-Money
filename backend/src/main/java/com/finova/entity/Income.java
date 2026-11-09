package com.finova.entity;

import com.finova.entity.enums.PaymentMethod;
import com.finova.entity.enums.RecurrencePeriod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "incomes", indexes = {
    @Index(name = "idx_income_user_date", columnList = "user_id, date")
})
public class Income {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 50)
    private String category; // Family Allowance, Part-time Job, Internship Stipend, Freelancing, Scholarship, etc.

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;

    @Column(length = 2000)
    private String description;

    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_period", length = 30)
    private RecurrencePeriod recurrencePeriod = RecurrencePeriod.NONE;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public Income() {}

    public Income(Long id, User user, String title, BigDecimal amount, String category, LocalDate date, PaymentMethod paymentMethod, String description, Boolean isRecurring, RecurrencePeriod recurrencePeriod, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.description = description;
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

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static IncomeBuilder builder() {
        return new IncomeBuilder();
    }

    public static class IncomeBuilder {
        private Long id;
        private User user;
        private String title;
        private BigDecimal amount;
        private String category;
        private LocalDate date;
        private PaymentMethod paymentMethod;
        private String description;
        private Boolean isRecurring;
        private RecurrencePeriod recurrencePeriod;
        private Instant createdAt;
        private Instant updatedAt;

        public IncomeBuilder() {}

        public IncomeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public IncomeBuilder user(User user) {
            this.user = user;
            return this;
        }

        public IncomeBuilder title(String title) {
            this.title = title;
            return this;
        }

        public IncomeBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public IncomeBuilder category(String category) {
            this.category = category;
            return this;
        }

        public IncomeBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public IncomeBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public IncomeBuilder description(String description) {
            this.description = description;
            return this;
        }

        public IncomeBuilder isRecurring(Boolean isRecurring) {
            this.isRecurring = isRecurring;
            return this;
        }

        public IncomeBuilder recurrencePeriod(RecurrencePeriod recurrencePeriod) {
            this.recurrencePeriod = recurrencePeriod;
            return this;
        }

        public IncomeBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public IncomeBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Income build() {
            Income instance = new Income();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setTitle(this.title);
            instance.setAmount(this.amount);
            instance.setCategory(this.category);
            instance.setDate(this.date);
            instance.setPaymentMethod(this.paymentMethod);
            instance.setDescription(this.description);
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
