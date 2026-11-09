package com.finova.integration.google.dto;

import java.time.Instant;

public class GoogleSyncLogDto {
    private Long id;
    private String serviceName;
    private String syncStatus;
    private String errorMessage;
    private String details;
    private Instant syncedAt;

    public GoogleSyncLogDto() {}

    public GoogleSyncLogDto(Long id, String serviceName, String syncStatus, String errorMessage, String details, Instant syncedAt) {
        this.id = id;
        this.serviceName = serviceName;
        this.syncStatus = syncStatus;
        this.errorMessage = errorMessage;
        this.details = details;
        this.syncedAt = syncedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Instant getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Instant syncedAt) { this.syncedAt = syncedAt; }
}
