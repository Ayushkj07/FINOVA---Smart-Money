package com.finova.integration.google.dto;

import java.time.Instant;

public class GoogleServiceStatusDto {
    private String serviceName;
    private String connectionStatus;
    private boolean isConnected;
    private String scopes;
    private Instant lastSyncAt;
    private String description;

    public GoogleServiceStatusDto() {}

    public GoogleServiceStatusDto(String serviceName, String connectionStatus, boolean isConnected, String scopes, Instant lastSyncAt, String description) {
        this.serviceName = serviceName;
        this.connectionStatus = connectionStatus;
        this.isConnected = isConnected;
        this.scopes = scopes;
        this.lastSyncAt = lastSyncAt;
        this.description = description;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getConnectionStatus() { return connectionStatus; }
    public void setConnectionStatus(String connectionStatus) { this.connectionStatus = connectionStatus; }

    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean connected) { isConnected = connected; }

    public String getScopes() { return scopes; }
    public void setScopes(String scopes) { this.scopes = scopes; }

    public Instant getLastSyncAt() { return lastSyncAt; }
    public void setLastSyncAt(Instant lastSyncAt) { this.lastSyncAt = lastSyncAt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
