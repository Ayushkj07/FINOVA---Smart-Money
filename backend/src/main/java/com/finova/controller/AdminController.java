package com.finova.controller;

import com.finova.dto.admin.*;
import com.finova.entity.AuditLog;
import com.finova.security.UserPrincipal;
import com.finova.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Portal", description = "Platform management, user status toggles, support ticketing, audit trail")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @Operation(summary = "Get platform-wide operational statistics")
    public ResponseEntity<AdminStatsResponse> getPlatformStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    @GetMapping("/users")
    @Operation(summary = "Get list of all platform user accounts")
    public ResponseEntity<List<UserAdminViewResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/toggle-status")
    @Operation(summary = "Activate or deactivate a student account")
    public ResponseEntity<UserAdminViewResponse> toggleUserStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.toggleUserStatus(userId));
    }

    @GetMapping("/tickets")
    @Operation(summary = "Get all student support and feedback tickets")
    public ResponseEntity<List<SupportTicketResponse>> getAllSupportTickets() {
        return ResponseEntity.ok(adminService.getAllSupportTickets());
    }

    @PostMapping("/tickets/{ticketId}/respond")
    @Operation(summary = "Submit an administrative response to a support ticket")
    public ResponseEntity<SupportTicketResponse> respondToTicket(
            @PathVariable Long ticketId,
            @RequestBody Map<String, String> payload) {
        String responseText = payload.get("response");
        String status = payload.getOrDefault("status", "RESOLVED");
        return ResponseEntity.ok(adminService.respondToTicket(ticketId, responseText, status));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "View application audit logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getRecentAuditLogs());
    }

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

}
