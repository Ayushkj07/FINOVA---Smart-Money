package com.finova.entity;

import com.finova.entity.enums.SentimentType;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "financial_journal")
public class FinancialJournal {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(nullable = false, length = 2000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SentimentType sentiment = SentimentType.NEUTRAL;

    @Column(length = 50)
    private String category = "GENERAL";

    @Column(length = 255)
    private String tags;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public FinancialJournal() {}

    public FinancialJournal(Long id, User user, String title, LocalDate entryDate, String notes, SentimentType sentiment, String category, String tags, Instant createdAt) {
        this.id = id;
        this.user = user;
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

    public static FinancialJournalBuilder builder() {
        return new FinancialJournalBuilder();
    }

    public static class FinancialJournalBuilder {
        private Long id;
        private User user;
        private String title;
        private LocalDate entryDate;
        private String notes;
        private SentimentType sentiment;
        private String category;
        private String tags;
        private Instant createdAt;

        public FinancialJournalBuilder() {}

        public FinancialJournalBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FinancialJournalBuilder user(User user) {
            this.user = user;
            return this;
        }

        public FinancialJournalBuilder title(String title) {
            this.title = title;
            return this;
        }

        public FinancialJournalBuilder entryDate(LocalDate entryDate) {
            this.entryDate = entryDate;
            return this;
        }

        public FinancialJournalBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public FinancialJournalBuilder sentiment(SentimentType sentiment) {
            this.sentiment = sentiment;
            return this;
        }

        public FinancialJournalBuilder category(String category) {
            this.category = category;
            return this;
        }

        public FinancialJournalBuilder tags(String tags) {
            this.tags = tags;
            return this;
        }

        public FinancialJournalBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FinancialJournal build() {
            FinancialJournal instance = new FinancialJournal();
            instance.setId(this.id);
            instance.setUser(this.user);
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
