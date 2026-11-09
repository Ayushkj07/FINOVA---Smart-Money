package com.finova.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceiptScanResponse {


    private String merchantName;
    private BigDecimal amount;
    private LocalDate date;
    private String suggestedCategory;
    private String notes;
    private String extractedRawText;
    private Double confidenceScore;


    public ReceiptScanResponse() {}

    public ReceiptScanResponse(String merchantName, BigDecimal amount, LocalDate date, String suggestedCategory, String notes, String extractedRawText, Double confidenceScore) {
        this.merchantName = merchantName;
        this.amount = amount;
        this.date = date;
        this.suggestedCategory = suggestedCategory;
        this.notes = notes;
        this.extractedRawText = extractedRawText;
        this.confidenceScore = confidenceScore;
    }

    public String getMerchantName() {
        return this.merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
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

    public String getSuggestedCategory() {
        return this.suggestedCategory;
    }

    public void setSuggestedCategory(String suggestedCategory) {
        this.suggestedCategory = suggestedCategory;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getExtractedRawText() {
        return this.extractedRawText;
    }

    public void setExtractedRawText(String extractedRawText) {
        this.extractedRawText = extractedRawText;
    }

    public Double getConfidenceScore() {
        return this.confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public static ReceiptScanResponseBuilder builder() {
        return new ReceiptScanResponseBuilder();
    }

    public static class ReceiptScanResponseBuilder {
        private String merchantName;
        private BigDecimal amount;
        private LocalDate date;
        private String suggestedCategory;
        private String notes;
        private String extractedRawText;
        private Double confidenceScore;

        public ReceiptScanResponseBuilder() {}

        public ReceiptScanResponseBuilder merchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public ReceiptScanResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ReceiptScanResponseBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public ReceiptScanResponseBuilder suggestedCategory(String suggestedCategory) {
            this.suggestedCategory = suggestedCategory;
            return this;
        }

        public ReceiptScanResponseBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ReceiptScanResponseBuilder extractedRawText(String extractedRawText) {
            this.extractedRawText = extractedRawText;
            return this;
        }

        public ReceiptScanResponseBuilder confidenceScore(Double confidenceScore) {
            this.confidenceScore = confidenceScore;
            return this;
        }

        public ReceiptScanResponse build() {
            ReceiptScanResponse instance = new ReceiptScanResponse();
            instance.setMerchantName(this.merchantName);
            instance.setAmount(this.amount);
            instance.setDate(this.date);
            instance.setSuggestedCategory(this.suggestedCategory);
            instance.setNotes(this.notes);
            instance.setExtractedRawText(this.extractedRawText);
            instance.setConfidenceScore(this.confidenceScore);
            return instance;
        }
    }
}
