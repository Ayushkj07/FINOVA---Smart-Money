package com.finova.integration.google.entity;

import com.finova.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "google_sync_logs")
public class GoogleSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName; // CALENDAR, SHEETS, DRIVE, GMAIL

    @Column(name = "sync_status", nullable = false, length = 30)
    private String syncStatus; // SUCCESS, FAILED, PARTIAL

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "synced_at", updatable = false)
    private Instant syncedAt = Instant.now();

    public GoogleSyncLog() {}

    public GoogleSyncLog(Long id, User user, String serviceName, String syncStatus, String errorMessage, String details, Instant syncedAt) {
        this.id = id;
        this.user = user;
        this.serviceName = serviceName;
        this.syncStatus = syncStatus;
        this.errorMessage = errorMessage;
        this.details = details;
        this.syncedAt = syncedAt != null ? syncedAt : Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public static GoogleSyncLogBuilder builder() {
        return new GoogleSyncLogBuilder();
    }

    public static class GoogleSyncLogBuilder {
        private Long id;
        private User user;
        private String serviceName;
        private String syncStatus;
        private String errorMessage;
        private String details;
        private Instant syncedAt;

        public GoogleSyncLogBuilder id(Long id) { this.id = id; return this; }
        public GoogleSyncLogBuilder user(User user) { this.user = user; return this; }
        public GoogleSyncLogBuilder serviceName(String serviceName) { this.serviceName = serviceName; return this; }
        public GoogleSyncLogBuilder syncStatus(String syncStatus) { this.syncStatus = syncStatus; return this; }
        public GoogleSyncLogBuilder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public GoogleSyncLogBuilder details(String details) { this.details = details; return this; }
        public GoogleSyncLogBuilder syncedAt(Instant syncedAt) { this.syncedAt = syncedAt; return this; }

        public GoogleSyncLog build() {
            return new GoogleSyncLog(id, user, serviceName, syncStatus, errorMessage, details, syncedAt);
        }
    }
}
