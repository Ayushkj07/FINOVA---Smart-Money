package com.finova.entity;

import com.finova.entity.enums.DebtStatus;
import com.finova.entity.enums.DebtType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "debts")
public class Debt {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DebtType type; // BORROWED, LENT

    @Column(name = "counterparty_name", nullable = false, length = 100)
    private String counterpartyName;

    @Column(name = "counterparty_contact", length = 100)
    private String counterpartyContact;

    @Column(name = "initial_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal initialAmount;

    @Column(name = "outstanding_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal outstandingBalance;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate = BigDecimal.ZERO;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DebtStatus status = DebtStatus.ACTIVE;

    @OneToMany(mappedBy = "debt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebtRepayment> repayments = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public Debt() {}

    public Debt(Long id, User user, DebtType type, String counterpartyName, String counterpartyContact, BigDecimal initialAmount, BigDecimal outstandingBalance, BigDecimal interestRate, LocalDate dueDate, String description, DebtStatus status, List<DebtRepayment> repayments, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.type = type;
        this.counterpartyName = counterpartyName;
        this.counterpartyContact = counterpartyContact;
        this.initialAmount = initialAmount;
        this.outstandingBalance = outstandingBalance;
        this.interestRate = interestRate;
        this.dueDate = dueDate;
        this.description = description;
        this.status = status;
        this.repayments = repayments;
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

    public DebtType getType() {
        return this.type;
    }

    public void setType(DebtType type) {
        this.type = type;
    }

    public String getCounterpartyName() {
        return this.counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public String getCounterpartyContact() {
        return this.counterpartyContact;
    }

    public void setCounterpartyContact(String counterpartyContact) {
        this.counterpartyContact = counterpartyContact;
    }

    public BigDecimal getInitialAmount() {
        return this.initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public BigDecimal getOutstandingBalance() {
        return this.outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DebtStatus getStatus() {
        return this.status;
    }

    public void setStatus(DebtStatus status) {
        this.status = status;
    }

    public List<DebtRepayment> getRepayments() {
        return this.repayments;
    }

    public void setRepayments(List<DebtRepayment> repayments) {
        this.repayments = repayments;
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

    public static DebtBuilder builder() {
        return new DebtBuilder();
    }

    public static class DebtBuilder {
        private Long id;
        private User user;
        private DebtType type;
        private String counterpartyName;
        private String counterpartyContact;
        private BigDecimal initialAmount;
        private BigDecimal outstandingBalance;
        private BigDecimal interestRate;
        private LocalDate dueDate;
        private String description;
        private DebtStatus status;
        private List<DebtRepayment> repayments;
        private Instant createdAt;
        private Instant updatedAt;

        public DebtBuilder() {}

        public DebtBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DebtBuilder user(User user) {
            this.user = user;
            return this;
        }

        public DebtBuilder type(DebtType type) {
            this.type = type;
            return this;
        }

        public DebtBuilder counterpartyName(String counterpartyName) {
            this.counterpartyName = counterpartyName;
            return this;
        }

        public DebtBuilder counterpartyContact(String counterpartyContact) {
            this.counterpartyContact = counterpartyContact;
            return this;
        }

        public DebtBuilder initialAmount(BigDecimal initialAmount) {
            this.initialAmount = initialAmount;
            return this;
        }

        public DebtBuilder outstandingBalance(BigDecimal outstandingBalance) {
            this.outstandingBalance = outstandingBalance;
            return this;
        }

        public DebtBuilder interestRate(BigDecimal interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public DebtBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public DebtBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DebtBuilder status(DebtStatus status) {
            this.status = status;
            return this;
        }

        public DebtBuilder repayments(List<DebtRepayment> repayments) {
            this.repayments = repayments;
            return this;
        }

        public DebtBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DebtBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Debt build() {
            Debt instance = new Debt();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setType(this.type);
            instance.setCounterpartyName(this.counterpartyName);
            instance.setCounterpartyContact(this.counterpartyContact);
            instance.setInitialAmount(this.initialAmount);
            instance.setOutstandingBalance(this.outstandingBalance);
            instance.setInterestRate(this.interestRate);
            instance.setDueDate(this.dueDate);
            instance.setDescription(this.description);
            instance.setStatus(this.status);
            instance.setRepayments(this.repayments);
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
