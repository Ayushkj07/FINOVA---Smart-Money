package com.finova.integration.google.entity;

import com.finova.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "google_integrations", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_service", columnNames = {"user_id", "service_name"})
})
public class GoogleIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName; // AUTH, CALENDAR, SHEETS, DRIVE, GMAIL, MAPS

    @Column(name = "connection_status", nullable = false, length = 30)
    private String connectionStatus = "DISCONNECTED"; // CONNECTED, DISCONNECTED, ERROR

    @Column(name = "scopes", length = 1000)
    private String scopes;

    @Column(name = "last_sync_at")
    private Instant lastSyncAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public GoogleIntegration() {}

    public GoogleIntegration(Long id, User user, String serviceName, String connectionStatus, String scopes, Instant lastSyncAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.serviceName = serviceName;
        this.connectionStatus = connectionStatus != null ? connectionStatus : "DISCONNECTED";
        this.scopes = scopes;
        this.lastSyncAt = lastSyncAt;
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

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getConnectionStatus() { return connectionStatus; }
    public void setConnectionStatus(String connectionStatus) { this.connectionStatus = connectionStatus; }

    public String getScopes() { return scopes; }
    public void setScopes(String scopes) { this.scopes = scopes; }

    public Instant getLastSyncAt() { return lastSyncAt; }
    public void setLastSyncAt(Instant lastSyncAt) { this.lastSyncAt = lastSyncAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static GoogleIntegrationBuilder builder() {
        return new GoogleIntegrationBuilder();
    }

    public static class GoogleIntegrationBuilder {
        private Long id;
        private User user;
        private String serviceName;
        private String connectionStatus;
        private String scopes;
        private Instant lastSyncAt;
        private Instant createdAt;
        private Instant updatedAt;

        public GoogleIntegrationBuilder id(Long id) { this.id = id; return this; }
        public GoogleIntegrationBuilder user(User user) { this.user = user; return this; }
        public GoogleIntegrationBuilder serviceName(String serviceName) { this.serviceName = serviceName; return this; }
        public GoogleIntegrationBuilder connectionStatus(String connectionStatus) { this.connectionStatus = connectionStatus; return this; }
        public GoogleIntegrationBuilder scopes(String scopes) { this.scopes = scopes; return this; }
        public GoogleIntegrationBuilder lastSyncAt(Instant lastSyncAt) { this.lastSyncAt = lastSyncAt; return this; }
        public GoogleIntegrationBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public GoogleIntegrationBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public GoogleIntegration build() {
            return new GoogleIntegration(id, user, serviceName, connectionStatus, scopes, lastSyncAt, createdAt, updatedAt);
        }
    }
}
