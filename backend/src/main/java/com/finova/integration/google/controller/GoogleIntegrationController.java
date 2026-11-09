package com.finova.integration.google.controller;

import com.finova.integration.google.dto.*;
import com.finova.integration.google.entity.GoogleSyncLog;
import com.finova.integration.google.repository.GoogleSyncLogRepository;
import com.finova.integration.google.service.*;
import com.finova.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/integrations/google")
@Tag(name = "Google Integration Suite", description = "Google OAuth2, Calendar, Sheets, Drive, and Gmail Integrations")
public class GoogleIntegrationController {

    private final GoogleOAuthService googleOAuthService;
    private final GoogleCalendarService googleCalendarService;
    private final GoogleSheetsService googleSheetsService;
    private final GoogleDriveService googleDriveService;
    private final GoogleGmailService googleGmailService;
    private final GoogleSyncLogRepository googleSyncLogRepository;

    public GoogleIntegrationController(GoogleOAuthService googleOAuthService,
                                      GoogleCalendarService googleCalendarService,
                                      GoogleSheetsService googleSheetsService,
                                      GoogleDriveService googleDriveService,
                                      GoogleGmailService googleGmailService,
                                      GoogleSyncLogRepository googleSyncLogRepository) {
        this.googleOAuthService = googleOAuthService;
        this.googleCalendarService = googleCalendarService;
        this.googleSheetsService = googleSheetsService;
        this.googleDriveService = googleDriveService;
        this.googleGmailService = googleGmailService;
        this.googleSyncLogRepository = googleSyncLogRepository;
    }

    @GetMapping("/status")
    @Operation(summary = "Get connection status of all Google integrations for current user")
    public ResponseEntity<GoogleIntegrationStatusResponse> getStatus(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(googleOAuthService.getIntegrationStatus(userPrincipal.getId()));
    }

    @GetMapping("/connect")
    @Operation(summary = "Generate Google OAuth authorization URL for incremental authorization")
    public ResponseEntity<GoogleConnectResponse> getConnectUrl(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "AUTH") String service) {
        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        return ResponseEntity.ok(googleOAuthService.getAuthorizationUrl(userId, service));
    }

    @PostMapping("/callback")
    @Operation(summary = "Exchange authorization code and link Google services")
    public ResponseEntity<Map<String, String>> handleCallback(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody GoogleCallbackRequest request) {
        googleOAuthService.handleCallback(userPrincipal.getId(), request.getCode(), request.getState(), request.getServiceName());
        return ResponseEntity.ok(Map.of("message", "Google service connected successfully."));
    }

    @PostMapping("/disconnect")
    @Operation(summary = "Disconnect a specific Google service")
    public ResponseEntity<Map<String, String>> disconnect(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "ALL") String service) {
        googleOAuthService.disconnectService(userPrincipal.getId(), service);
        return ResponseEntity.ok(Map.of("message", "Disconnected Google service: " + service));
    }

    @PostMapping("/tokens/revoke")
    @Operation(summary = "Revoke all Google tokens and disconnect all integrations")
    public ResponseEntity<Map<String, String>> revokeTokens(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        googleOAuthService.revokeAllPermissions(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("message", "All Google permissions have been revoked."));
    }

    // --- Google Calendar Endpoints ---
    @PostMapping("/calendar/sync")
    @Operation(summary = "Trigger two-way synchronization of financial deadlines with Google Calendar")
    public ResponseEntity<GoogleCalendarSyncResult> syncCalendar(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(googleCalendarService.syncFinancialEvents(userPrincipal.getId()));
    }

    @GetMapping("/calendar/events")
    @Operation(summary = "Get all upcoming synced calendar events")
    public ResponseEntity<List<GoogleCalendarEventDto>> getCalendarEvents(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(googleCalendarService.getUpcomingSyncedEvents(userPrincipal.getId()));
    }

    @DeleteMapping("/calendar/events/{id}")
    @Operation(summary = "Delete a synchronized Google Calendar event")
    public ResponseEntity<Map<String, String>> deleteCalendarEvent(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        googleCalendarService.deleteCalendarEvent(userPrincipal.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Event deleted from calendar."));
    }

    // --- Google Sheets Endpoints ---
    @PostMapping("/sheets/export")
    @Operation(summary = "Export comprehensive 7-tab financial workbook to Google Sheets")
    public ResponseEntity<GoogleSheetsExportResponse> exportSheets(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody(required = false) Map<String, String> body) {
        String title = (body != null) ? body.get("title") : null;
        return ResponseEntity.ok(googleSheetsService.exportFullFinancialData(userPrincipal.getId(), title));
    }

    // --- Google Drive Endpoints ---
    @PostMapping("/drive/upload")
    @Operation(summary = "Upload PDF/Excel financial statement or report to FINOVA folder in Google Drive")
    public ResponseEntity<GoogleDriveUploadResponse> uploadToDrive(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody(required = false) Map<String, String> body) {
        String reportType = (body != null && body.containsKey("reportType")) ? body.get("reportType") : "FINANCIAL_REPORT";
        String format = (body != null && body.containsKey("format")) ? body.get("format") : "PDF";
        return ResponseEntity.ok(googleDriveService.uploadGeneratedReport(userPrincipal.getId(), reportType, format));
    }

    @GetMapping("/drive/files")
    @Operation(summary = "List all FINOVA financial files stored in Google Drive")
    public ResponseEntity<List<GoogleDriveFileDto>> listDriveFiles(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(googleDriveService.listFiles(userPrincipal.getId()));
    }

    @DeleteMapping("/drive/files/{id}")
    @Operation(summary = "Delete a financial file from Google Drive")
    public ResponseEntity<Map<String, String>> deleteDriveFile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        googleDriveService.deleteFile(userPrincipal.getId(), id);
        return ResponseEntity.ok(Map.of("message", "File deleted from Google Drive."));
    }

    // --- Gmail Notification Endpoints ---
    @PostMapping("/gmail/send")
    @Operation(summary = "Send an email notification via Gmail API")
    public ResponseEntity<Map<String, String>> sendGmail(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody GoogleSendEmailRequest request) {
        googleGmailService.sendEmail(userPrincipal.getId(), request);
        return ResponseEntity.ok(Map.of("message", "Notification email dispatched successfully."));
    }

    @PostMapping("/gmail/monthly-summary")
    @Operation(summary = "Send current monthly financial performance summary via Gmail")
    public ResponseEntity<Map<String, String>> sendMonthlySummary(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        googleGmailService.sendMonthlySummaryEmail(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("message", "Monthly financial summary sent to your email."));
    }

    // --- Sync Activity History ---
    @GetMapping("/sync-history")
    @Operation(summary = "Get recent Google service sync activity logs")
    public ResponseEntity<List<GoogleSyncLogDto>> getSyncHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<GoogleSyncLog> logs = googleSyncLogRepository.findTop20ByUserIdOrderBySyncedAtDesc(userPrincipal.getId());
        List<GoogleSyncLogDto> dtos = logs.stream().map(l -> new GoogleSyncLogDto(
                l.getId(),
                l.getServiceName(),
                l.getSyncStatus(),
                l.getErrorMessage(),
                l.getDetails(),
                l.getSyncedAt()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
