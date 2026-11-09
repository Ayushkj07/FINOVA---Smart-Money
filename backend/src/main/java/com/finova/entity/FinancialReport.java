package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "financial_reports")
public class FinancialReport {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType; // MONTHLY, QUARTERLY, ANNUAL

    @Column(name = "period_label", nullable = false, length = 50)
    private String periodLabel;

    @Column(name = "total_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalIncome;

    @Column(name = "total_expenses", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalExpenses;

    @Column(name = "net_savings", nullable = false, precision = 12, scale = 2)
    private BigDecimal netSavings;

    @Column(name = "savings_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal savingsRate;

    @Column(name = "summary_json", length = 2000)
    private String summaryJson;

    @Column(name = "generated_at", updatable = false)
    private Instant generatedAt = Instant.now();


    public FinancialReport() {}

    public FinancialReport(Long id, User user, String reportType, String periodLabel, BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netSavings, BigDecimal savingsRate, String summaryJson, Instant generatedAt) {
        this.id = id;
        this.user = user;
        this.reportType = reportType;
        this.periodLabel = periodLabel;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netSavings = netSavings;
        this.savingsRate = savingsRate;
        this.summaryJson = summaryJson;
        this.generatedAt = generatedAt;
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

    public String getReportType() {
        return this.reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getPeriodLabel() {
        return this.periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public BigDecimal getTotalIncome() {
        return this.totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return this.totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetSavings() {
        return this.netSavings;
    }

    public void setNetSavings(BigDecimal netSavings) {
        this.netSavings = netSavings;
    }

    public BigDecimal getSavingsRate() {
        return this.savingsRate;
    }

    public void setSavingsRate(BigDecimal savingsRate) {
        this.savingsRate = savingsRate;
    }

    public String getSummaryJson() {
        return this.summaryJson;
    }

    public void setSummaryJson(String summaryJson) {
        this.summaryJson = summaryJson;
    }

    public Instant getGeneratedAt() {
        return this.generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public static FinancialReportBuilder builder() {
        return new FinancialReportBuilder();
    }

    public static class FinancialReportBuilder {
        private Long id;
        private User user;
        private String reportType;
        private String periodLabel;
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netSavings;
        private BigDecimal savingsRate;
        private String summaryJson;
        private Instant generatedAt;

        public FinancialReportBuilder() {}

        public FinancialReportBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FinancialReportBuilder user(User user) {
            this.user = user;
            return this;
        }

        public FinancialReportBuilder reportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public FinancialReportBuilder periodLabel(String periodLabel) {
            this.periodLabel = periodLabel;
            return this;
        }

        public FinancialReportBuilder totalIncome(BigDecimal totalIncome) {
            this.totalIncome = totalIncome;
            return this;
        }

        public FinancialReportBuilder totalExpenses(BigDecimal totalExpenses) {
            this.totalExpenses = totalExpenses;
            return this;
        }

        public FinancialReportBuilder netSavings(BigDecimal netSavings) {
            this.netSavings = netSavings;
            return this;
        }

        public FinancialReportBuilder savingsRate(BigDecimal savingsRate) {
            this.savingsRate = savingsRate;
            return this;
        }

        public FinancialReportBuilder summaryJson(String summaryJson) {
            this.summaryJson = summaryJson;
            return this;
        }

        public FinancialReportBuilder generatedAt(Instant generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        public FinancialReport build() {
            FinancialReport instance = new FinancialReport();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setReportType(this.reportType);
            instance.setPeriodLabel(this.periodLabel);
            instance.setTotalIncome(this.totalIncome);
            instance.setTotalExpenses(this.totalExpenses);
            instance.setNetSavings(this.netSavings);
            instance.setSavingsRate(this.savingsRate);
            instance.setSummaryJson(this.summaryJson);
            instance.setGeneratedAt(this.generatedAt);
            return instance;
        }
    }
}
