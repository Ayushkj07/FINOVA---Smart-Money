package com.finova.integration.google.entity;

import com.finova.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "google_calendar_events", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_record_type", columnNames = {"user_id", "financial_record_id", "event_type"})
})
public class GoogleCalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "financial_record_id", nullable = false)
    private Long financialRecordId;

    @Column(name = "google_event_id", nullable = false, length = 150)
    private String googleEventId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // SUBSCRIPTION_RENEWAL, LOAN_REPAYMENT, BUDGET_REVIEW, SAVINGS_DEADLINE

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "sync_status", nullable = false, length = 30)
    private String syncStatus = "SYNCED"; // SYNCED, PENDING, FAILED

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public GoogleCalendarEvent() {}

    public GoogleCalendarEvent(Long id, User user, Long financialRecordId, String googleEventId, String eventType, LocalDate eventDate, String syncStatus, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.financialRecordId = financialRecordId;
        this.googleEventId = googleEventId;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.syncStatus = syncStatus != null ? syncStatus : "SYNCED";
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getFinancialRecordId() { return financialRecordId; }
    public void setFinancialRecordId(Long financialRecordId) { this.financialRecordId = financialRecordId; }

    public String getGoogleEventId() { return googleEventId; }
    public void setGoogleEventId(String googleEventId) { this.googleEventId = googleEventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static GoogleCalendarEventBuilder builder() {
        return new GoogleCalendarEventBuilder();
    }

    public static class GoogleCalendarEventBuilder {
        private Long id;
        private User user;
        private Long financialRecordId;
        private String googleEventId;
        private String eventType;
        private LocalDate eventDate;
        private String syncStatus;
        private Instant createdAt;
        private Instant updatedAt;

        public GoogleCalendarEventBuilder id(Long id) { this.id = id; return this; }
        public GoogleCalendarEventBuilder user(User user) { this.user = user; return this; }
        public GoogleCalendarEventBuilder financialRecordId(Long financialRecordId) { this.financialRecordId = financialRecordId; return this; }
        public GoogleCalendarEventBuilder googleEventId(String googleEventId) { this.googleEventId = googleEventId; return this; }
        public GoogleCalendarEventBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public GoogleCalendarEventBuilder eventDate(LocalDate eventDate) { this.eventDate = eventDate; return this; }
        public GoogleCalendarEventBuilder syncStatus(String syncStatus) { this.syncStatus = syncStatus; return this; }
        public GoogleCalendarEventBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public GoogleCalendarEventBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public GoogleCalendarEvent build() {
            return new GoogleCalendarEvent(id, user, financialRecordId, googleEventId, eventType, eventDate, syncStatus, createdAt, updatedAt);
        }
    }
}
