package com.finova.integration.google.dto;

import java.time.Instant;
import java.time.LocalDate;

public class GoogleCalendarEventDto {
    private Long id;
    private Long financialRecordId;
    private String googleEventId;
    private String eventType;
    private LocalDate eventDate;
    private String syncStatus;
    private String title;
    private String description;
    private Instant createdAt;

    public GoogleCalendarEventDto() {}

    public GoogleCalendarEventDto(Long id, Long financialRecordId, String googleEventId, String eventType, LocalDate eventDate, String syncStatus, String title, String description, Instant createdAt) {
        this.id = id;
        this.financialRecordId = financialRecordId;
        this.googleEventId = googleEventId;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.syncStatus = syncStatus;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
