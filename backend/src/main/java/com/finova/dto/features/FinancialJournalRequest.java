package com.finova.dto.features;

import com.finova.entity.enums.SentimentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class FinancialJournalRequest {



    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;

    @NotBlank(message = "Notes cannot be empty")
    private String notes;

    private SentimentType sentiment = SentimentType.NEUTRAL;

    private String category = "GENERAL";

    private String tags;


    public FinancialJournalRequest() {}

    public FinancialJournalRequest(String title, LocalDate entryDate, String notes, SentimentType sentiment, String category, String tags) {
        this.title = title;
        this.entryDate = entryDate;
        this.notes = notes;
        this.sentiment = sentiment;
        this.category = category;
        this.tags = tags;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getEntryDate() {
        return this.entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public SentimentType getSentiment() {
        return this.sentiment;
    }

    public void setSentiment(SentimentType sentiment) {
        this.sentiment = sentiment;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public static FinancialJournalRequestBuilder builder() {
        return new FinancialJournalRequestBuilder();
    }

    public static class FinancialJournalRequestBuilder {
        private String title;
        private LocalDate entryDate;
        private String notes;
        private SentimentType sentiment;
        private String category;
        private String tags;

        public FinancialJournalRequestBuilder() {}

        public FinancialJournalRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public FinancialJournalRequestBuilder entryDate(LocalDate entryDate) {
            this.entryDate = entryDate;
            return this;
        }

        public FinancialJournalRequestBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public FinancialJournalRequestBuilder sentiment(SentimentType sentiment) {
            this.sentiment = sentiment;
            return this;
        }

        public FinancialJournalRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public FinancialJournalRequestBuilder tags(String tags) {
            this.tags = tags;
            return this;
        }

        public FinancialJournalRequest build() {
            FinancialJournalRequest instance = new FinancialJournalRequest();
            instance.setTitle(this.title);
            instance.setEntryDate(this.entryDate);
            instance.setNotes(this.notes);
            instance.setSentiment(this.sentiment);
            instance.setCategory(this.category);
            instance.setTags(this.tags);
            return instance;
        }
    }
}
