package com.finova.integration.google.dto;

import java.util.List;

public class GoogleCalendarSyncResult {
    private int syncedCount;
    private int updatedCount;
    private int deletedCount;
    private int errorsCount;
    private String message;
    private List<String> details;

    public GoogleCalendarSyncResult() {}

    public GoogleCalendarSyncResult(int syncedCount, int updatedCount, int deletedCount, int errorsCount, String message, List<String> details) {
        this.syncedCount = syncedCount;
        this.updatedCount = updatedCount;
        this.deletedCount = deletedCount;
        this.errorsCount = errorsCount;
        this.message = message;
        this.details = details;
    }

    public int getSyncedCount() { return syncedCount; }
    public void setSyncedCount(int syncedCount) { this.syncedCount = syncedCount; }

    public int getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }

    public int getDeletedCount() { return deletedCount; }
    public void setDeletedCount(int deletedCount) { this.deletedCount = deletedCount; }

    public int getErrorsCount() { return errorsCount; }
    public void setErrorsCount(int errorsCount) { this.errorsCount = errorsCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
