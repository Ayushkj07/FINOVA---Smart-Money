package com.finova.dto.debt;

import com.finova.entity.enums.DebtType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DebtRequest {



    @NotNull(message = "Debt type is required (BORROWED or LENT)")
    private DebtType type;

    @NotBlank(message = "Counterparty name is required")
    private String counterpartyName;

    private String counterpartyContact;

    @NotNull(message = "Initial amount is required")
    @Positive(message = "Initial amount must be positive")
    private BigDecimal initialAmount;

    private BigDecimal interestRate = BigDecimal.ZERO;

    private LocalDate dueDate;

    private String description;


    public DebtRequest() {}

    public DebtRequest(DebtType type, String counterpartyName, String counterpartyContact, BigDecimal initialAmount, BigDecimal interestRate, LocalDate dueDate, String description) {
        this.type = type;
        this.counterpartyName = counterpartyName;
        this.counterpartyContact = counterpartyContact;
        this.initialAmount = initialAmount;
        this.interestRate = interestRate;
        this.dueDate = dueDate;
        this.description = description;
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

    public static DebtRequestBuilder builder() {
        return new DebtRequestBuilder();
    }

    public static class DebtRequestBuilder {
        private DebtType type;
        private String counterpartyName;
        private String counterpartyContact;
        private BigDecimal initialAmount;
        private BigDecimal interestRate;
        private LocalDate dueDate;
        private String description;

        public DebtRequestBuilder() {}

        public DebtRequestBuilder type(DebtType type) {
            this.type = type;
            return this;
        }

        public DebtRequestBuilder counterpartyName(String counterpartyName) {
            this.counterpartyName = counterpartyName;
            return this;
        }

        public DebtRequestBuilder counterpartyContact(String counterpartyContact) {
            this.counterpartyContact = counterpartyContact;
            return this;
        }

        public DebtRequestBuilder initialAmount(BigDecimal initialAmount) {
            this.initialAmount = initialAmount;
            return this;
        }

        public DebtRequestBuilder interestRate(BigDecimal interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public DebtRequestBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public DebtRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DebtRequest build() {
            DebtRequest instance = new DebtRequest();
            instance.setType(this.type);
            instance.setCounterpartyName(this.counterpartyName);
            instance.setCounterpartyContact(this.counterpartyContact);
            instance.setInitialAmount(this.initialAmount);
            instance.setInterestRate(this.interestRate);
            instance.setDueDate(this.dueDate);
            instance.setDescription(this.description);
            return instance;
        }
    }
}
