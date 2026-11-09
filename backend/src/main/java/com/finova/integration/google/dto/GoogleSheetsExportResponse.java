package com.finova.integration.google.dto;

public class GoogleSheetsExportResponse {
    private String spreadsheetId;
    private String spreadsheetUrl;
    private String title;
    private int sheetsExportedCount;
    private String message;

    public GoogleSheetsExportResponse() {}

    public GoogleSheetsExportResponse(String spreadsheetId, String spreadsheetUrl, String title, int sheetsExportedCount, String message) {
        this.spreadsheetId = spreadsheetId;
        this.spreadsheetUrl = spreadsheetUrl;
        this.title = title;
        this.sheetsExportedCount = sheetsExportedCount;
        this.message = message;
    }

    public String getSpreadsheetId() { return spreadsheetId; }
    public void setSpreadsheetId(String spreadsheetId) { this.spreadsheetId = spreadsheetId; }

    public String getSpreadsheetUrl() { return spreadsheetUrl; }
    public void setSpreadsheetUrl(String spreadsheetUrl) { this.spreadsheetUrl = spreadsheetUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getSheetsExportedCount() { return sheetsExportedCount; }
    public void setSheetsExportedCount(int sheetsExportedCount) { this.sheetsExportedCount = sheetsExportedCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
