package com.finova.dto.debt;

import com.finova.entity.enums.DebtStatus;
import com.finova.entity.enums.DebtType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DebtResponse {

    public static class RepaymentDto {
        private Long id;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String notes;

        public RepaymentDto() {}
        public RepaymentDto(Long id, BigDecimal amount, LocalDate paymentDate, String notes) {
            this.id = id;
            this.amount = amount;
            this.paymentDate = paymentDate;
            this.notes = notes;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public LocalDate getPaymentDate() { return paymentDate; }
        public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public static RepaymentDtoBuilder builder() { return new RepaymentDtoBuilder(); }
        public static class RepaymentDtoBuilder {
            private Long id;
            private BigDecimal amount;
            private LocalDate paymentDate;
            private String notes;

            public RepaymentDtoBuilder id(Long id) { this.id = id; return this; }
            public RepaymentDtoBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
            public RepaymentDtoBuilder paymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; return this; }
            public RepaymentDtoBuilder notes(String notes) { this.notes = notes; return this; }
            public RepaymentDto build() { return new RepaymentDto(id, amount, paymentDate, notes); }
        }
    }

    private Long id;
    private DebtType type;
    private String counterpartyName;
    private String counterpartyContact;
    private BigDecimal initialAmount;
    private BigDecimal outstandingBalance;
    private BigDecimal totalPaid;
    private BigDecimal interestRate;
    private LocalDate dueDate;
    private String description;
    private DebtStatus status;
    private List<RepaymentDto> repayments;

    public DebtResponse() {}
    public DebtResponse(Long id, DebtType type, String counterpartyName, String counterpartyContact, BigDecimal initialAmount, BigDecimal outstandingBalance, BigDecimal totalPaid, BigDecimal interestRate, LocalDate dueDate, String description, DebtStatus status, List<RepaymentDto> repayments) {
        this.id = id;
        this.type = type;
        this.counterpartyName = counterpartyName;
        this.counterpartyContact = counterpartyContact;
        this.initialAmount = initialAmount;
        this.outstandingBalance = outstandingBalance;
        this.totalPaid = totalPaid;
        this.interestRate = interestRate;
        this.dueDate = dueDate;
        this.description = description;
        this.status = status;
        this.repayments = repayments;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DebtType getType() { return type; }
    public void setType(DebtType type) { this.type = type; }
    public String getCounterpartyName() { return counterpartyName; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }
    public String getCounterpartyContact() { return counterpartyContact; }
    public void setCounterpartyContact(String counterpartyContact) { this.counterpartyContact = counterpartyContact; }
    public BigDecimal getInitialAmount() { return initialAmount; }
    public void setInitialAmount(BigDecimal initialAmount) { this.initialAmount = initialAmount; }
    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public DebtStatus getStatus() { return status; }
    public void setStatus(DebtStatus status) { this.status = status; }
    public List<RepaymentDto> getRepayments() { return repayments; }
    public void setRepayments(List<RepaymentDto> repayments) { this.repayments = repayments; }

    public static DebtResponseBuilder builder() { return new DebtResponseBuilder(); }
    public static class DebtResponseBuilder {
        private Long id;
        private DebtType type;
        private String counterpartyName;
        private String counterpartyContact;
        private BigDecimal initialAmount;
        private BigDecimal outstandingBalance;
        private BigDecimal totalPaid;
        private BigDecimal interestRate;
        private LocalDate dueDate;
        private String description;
        private DebtStatus status;
        private List<RepaymentDto> repayments;

        public DebtResponseBuilder id(Long id) { this.id = id; return this; }
        public DebtResponseBuilder type(DebtType type) { this.type = type; return this; }
        public DebtResponseBuilder counterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; return this; }
        public DebtResponseBuilder counterpartyContact(String counterpartyContact) { this.counterpartyContact = counterpartyContact; return this; }
        public DebtResponseBuilder initialAmount(BigDecimal initialAmount) { this.initialAmount = initialAmount; return this; }
        public DebtResponseBuilder outstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; return this; }
        public DebtResponseBuilder totalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; return this; }
        public DebtResponseBuilder interestRate(BigDecimal interestRate) { this.interestRate = interestRate; return this; }
        public DebtResponseBuilder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public DebtResponseBuilder description(String description) { this.description = description; return this; }
        public DebtResponseBuilder status(DebtStatus status) { this.status = status; return this; }
        public DebtResponseBuilder repayments(List<RepaymentDto> repayments) { this.repayments = repayments; return this; }
        public DebtResponse build() { return new DebtResponse(id, type, counterpartyName, counterpartyContact, initialAmount, outstandingBalance, totalPaid, interestRate, dueDate, description, status, repayments); }
    }
}
