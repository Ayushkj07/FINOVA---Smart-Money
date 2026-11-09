package com.finova.service;

import com.finova.dto.admin.*;
import com.finova.entity.AuditLog;

import java.util.List;

public interface AdminService {
    AdminStatsResponse getPlatformStats();
    List<UserAdminViewResponse> getAllUsers();
    UserAdminViewResponse toggleUserStatus(Long userId);
    List<SupportTicketResponse> getAllSupportTickets();
    SupportTicketResponse createSupportTicket(Long userId, SupportTicketRequest request);
    SupportTicketResponse respondToTicket(Long ticketId, String responseText, String status);
    List<AuditLog> getRecentAuditLogs();
}
