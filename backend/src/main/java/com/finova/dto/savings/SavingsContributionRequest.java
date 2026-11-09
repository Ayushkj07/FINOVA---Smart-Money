package com.finova.dto.savings;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsContributionRequest {



    @NotNull(message = "Contribution amount is required")
    @Positive(message = "Contribution amount must be positive")
    private BigDecimal amount;

    private LocalDate contributionDate = LocalDate.now();

    private String notes;


    public SavingsContributionRequest() {}

    public SavingsContributionRequest(BigDecimal amount, LocalDate contributionDate, String notes) {
        this.amount = amount;
        this.contributionDate = contributionDate;
        this.notes = notes;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getContributionDate() {
        return this.contributionDate;
    }

    public void setContributionDate(LocalDate contributionDate) {
        this.contributionDate = contributionDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static SavingsContributionRequestBuilder builder() {
        return new SavingsContributionRequestBuilder();
    }

    public static class SavingsContributionRequestBuilder {
        private BigDecimal amount;
        private LocalDate contributionDate;
        private String notes;

        public SavingsContributionRequestBuilder() {}

        public SavingsContributionRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SavingsContributionRequestBuilder contributionDate(LocalDate contributionDate) {
            this.contributionDate = contributionDate;
            return this;
        }

        public SavingsContributionRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public SavingsContributionRequest build() {
            SavingsContributionRequest instance = new SavingsContributionRequest();
            instance.setAmount(this.amount);
            instance.setContributionDate(this.contributionDate);
            instance.setNotes(this.notes);
            return instance;
        }
    }
}
