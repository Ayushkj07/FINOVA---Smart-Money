package com.finova.dto.features;

import com.finova.entity.enums.SentimentType;
import java.time.Instant;
import java.time.LocalDate;

public class FinancialJournalResponse {


    private Long id;
    private String title;
    private LocalDate entryDate;
    private String notes;
    private SentimentType sentiment;
    private String category;
    private String tags;
    private Instant createdAt;


    public FinancialJournalResponse() {}

    public FinancialJournalResponse(Long id, String title, LocalDate entryDate, String notes, SentimentType sentiment, String category, String tags, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.entryDate = entryDate;
        this.notes = notes;
        this.sentiment = sentiment;
        this.category = category;
        this.tags = tags;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static FinancialJournalResponseBuilder builder() {
        return new FinancialJournalResponseBuilder();
    }

    public static class FinancialJournalResponseBuilder {
        private Long id;
        private String title;
        private LocalDate entryDate;
        private String notes;
        private SentimentType sentiment;
        private String category;
        private String tags;
        private Instant createdAt;

        public FinancialJournalResponseBuilder() {}

        public FinancialJournalResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FinancialJournalResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public FinancialJournalResponseBuilder entryDate(LocalDate entryDate) {
            this.entryDate = entryDate;
            return this;
        }

        public FinancialJournalResponseBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public FinancialJournalResponseBuilder sentiment(SentimentType sentiment) {
            this.sentiment = sentiment;
            return this;
        }

        public FinancialJournalResponseBuilder category(String category) {
            this.category = category;
            return this;
        }

        public FinancialJournalResponseBuilder tags(String tags) {
            this.tags = tags;
            return this;
        }

        public FinancialJournalResponseBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FinancialJournalResponse build() {
            FinancialJournalResponse instance = new FinancialJournalResponse();
            instance.setId(this.id);
            instance.setTitle(this.title);
            instance.setEntryDate(this.entryDate);
            instance.setNotes(this.notes);
            instance.setSentiment(this.sentiment);
            instance.setCategory(this.category);
            instance.setTags(this.tags);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
