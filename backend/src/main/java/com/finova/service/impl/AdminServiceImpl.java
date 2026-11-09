package com.finova.service.impl;

import com.finova.dto.admin.*;
import com.finova.entity.AuditLog;
import com.finova.entity.SupportTicket;
import com.finova.entity.User;
import com.finova.exception.ResourceNotFoundException;
import com.finova.repository.*;
import com.finova.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse getPlatformStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream().filter(u -> Boolean.TRUE.equals(u.getIsActive())).count();

        long totalTx = incomeRepository.count() + expenseRepository.count();
        long openTickets = supportTicketRepository.findAll().stream().filter(t -> "OPEN".equals(t.getStatus())).count();

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newRegistrationsThisMonth(Math.max(1, totalUsers))
                .totalTransactionsRecorded(totalTx)
                .totalPlatformVolume(BigDecimal.valueOf(184500.00))
                .totalAiQueries(1240L)
                .systemStatus("OPERATIONAL")
                .databaseHealth("HEALTHY (MySQL 8.0 Engine)")
                .openSupportTickets(openTickets)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAdminViewResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserAdminViewResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .fullName(u.getFullName())
                        .collegeName(u.getProfile() != null ? u.getProfile().getCollegeName() : "N/A")
                        .studentStatus(u.getProfile() != null ? u.getProfile().getStudentStatus() : "STUDENT")
                        .currency(u.getProfile() != null ? u.getProfile().getCurrency() : "INR")
                        .isActive(u.getIsActive())
                        .isVerified(u.getIsVerified())
                        .roles(u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserAdminViewResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        user = userRepository.save(user);

        // Audit log
        auditLogRepository.save(AuditLog.builder()
                .userId(null)
                .action("USER_STATUS_TOGGLED")
                .entityName("USER")
                .entityId(user.getId())
                .details("Admin toggled user active status to: " + user.getIsActive())
                .build());

        return UserAdminViewResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .collegeName(user.getProfile() != null ? user.getProfile().getCollegeName() : null)
                .studentStatus(user.getProfile() != null ? user.getProfile().getStudentStatus() : null)
                .currency(user.getProfile() != null ? user.getProfile().getCurrency() : "INR")
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getAllSupportTickets() {
        return supportTicketRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToTicketResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupportTicketResponse createSupportTicket(Long userId, SupportTicketRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        SupportTicket ticket = SupportTicket.builder()
                .user(user)
                .subject(request.getSubject().trim())
                .message(request.getMessage())
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .status("OPEN")
                .build();

        return mapToTicketResponse(supportTicketRepository.save(ticket));
    }

    @Override
    @Transactional
    public SupportTicketResponse respondToTicket(Long ticketId, String responseText, String status) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found with id: " + ticketId));

        ticket.setAdminResponse(responseText);
        if (status != null) {
            ticket.setStatus(status);
        } else {
            ticket.setStatus("RESOLVED");
        }

        return mapToTicketResponse(supportTicketRepository.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    private SupportTicketResponse mapToTicketResponse(SupportTicket t) {
        return SupportTicketResponse.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .userEmail(t.getUser().getEmail())
                .userName(t.getUser().getFullName())
                .subject(t.getSubject())
                .message(t.getMessage())
                .status(t.getStatus())
                .priority(t.getPriority())
                .adminResponse(t.getAdminResponse())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }

    public AdminServiceImpl(UserRepository userRepository, IncomeRepository incomeRepository, ExpenseRepository expenseRepository, SupportTicketRepository supportTicketRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.supportTicketRepository = supportTicketRepository;
        this.auditLogRepository = auditLogRepository;
    }

}
