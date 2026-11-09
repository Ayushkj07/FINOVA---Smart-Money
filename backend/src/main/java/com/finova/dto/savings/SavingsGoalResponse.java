package com.finova.dto.savings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SavingsGoalResponse {

    public static class ContributionDto {
        private Long id;
        private BigDecimal amount;
        private LocalDate contributionDate;
        private String notes;

        public ContributionDto() {}
        public ContributionDto(Long id, BigDecimal amount, LocalDate contributionDate, String notes) {
            this.id = id;
            this.amount = amount;
            this.contributionDate = contributionDate;
            this.notes = notes;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public LocalDate getContributionDate() { return contributionDate; }
        public void setContributionDate(LocalDate contributionDate) { this.contributionDate = contributionDate; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public static ContributionDtoBuilder builder() { return new ContributionDtoBuilder(); }
        public static class ContributionDtoBuilder {
            private Long id;
            private BigDecimal amount;
            private LocalDate contributionDate;
            private String notes;

            public ContributionDtoBuilder id(Long id) { this.id = id; return this; }
            public ContributionDtoBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
            public ContributionDtoBuilder contributionDate(LocalDate contributionDate) { this.contributionDate = contributionDate; return this; }
            public ContributionDtoBuilder notes(String notes) { this.notes = notes; return this; }
            public ContributionDto build() { return new ContributionDto(id, amount, contributionDate, notes); }
        }
    }

    private Long id;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal remainingAmount;
    private Double progressPercentage;
    private LocalDate targetDate;
    private String estimatedCompletionDate;
    private String category;
    private String description;
    private String status;
    private List<ContributionDto> contributions;

    public SavingsGoalResponse() {}
    public SavingsGoalResponse(Long id, String name, BigDecimal targetAmount, BigDecimal currentAmount, BigDecimal remainingAmount, Double progressPercentage, LocalDate targetDate, String estimatedCompletionDate, String category, String description, String status, List<ContributionDto> contributions) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.remainingAmount = remainingAmount;
        this.progressPercentage = progressPercentage;
        this.targetDate = targetDate;
        this.estimatedCompletionDate = estimatedCompletionDate;
        this.category = category;
        this.description = description;
        this.status = status;
        this.contributions = contributions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public String getEstimatedCompletionDate() { return estimatedCompletionDate; }
    public void setEstimatedCompletionDate(String estimatedCompletionDate) { this.estimatedCompletionDate = estimatedCompletionDate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<ContributionDto> getContributions() { return contributions; }
    public void setContributions(List<ContributionDto> contributions) { this.contributions = contributions; }

    public static SavingsGoalResponseBuilder builder() { return new SavingsGoalResponseBuilder(); }
    public static class SavingsGoalResponseBuilder {
        private Long id;
        private String name;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private BigDecimal remainingAmount;
        private Double progressPercentage;
        private LocalDate targetDate;
        private String estimatedCompletionDate;
        private String category;
        private String description;
        private String status;
        private List<ContributionDto> contributions;

        public SavingsGoalResponseBuilder id(Long id) { this.id = id; return this; }
        public SavingsGoalResponseBuilder name(String name) { this.name = name; return this; }
        public SavingsGoalResponseBuilder targetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; return this; }
        public SavingsGoalResponseBuilder currentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; return this; }
        public SavingsGoalResponseBuilder remainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; return this; }
        public SavingsGoalResponseBuilder progressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; return this; }
        public SavingsGoalResponseBuilder targetDate(LocalDate targetDate) { this.targetDate = targetDate; return this; }
        public SavingsGoalResponseBuilder estimatedCompletionDate(String estimatedCompletionDate) { this.estimatedCompletionDate = estimatedCompletionDate; return this; }
        public SavingsGoalResponseBuilder category(String category) { this.category = category; return this; }
        public SavingsGoalResponseBuilder description(String description) { this.description = description; return this; }
        public SavingsGoalResponseBuilder status(String status) { this.status = status; return this; }
        public SavingsGoalResponseBuilder contributions(List<ContributionDto> contributions) { this.contributions = contributions; return this; }
        public SavingsGoalResponse build() { return new SavingsGoalResponse(id, name, targetAmount, currentAmount, remainingAmount, progressPercentage, targetDate, estimatedCompletionDate, category, description, status, contributions); }
    }
}
