package com.finova.integration.google.service;

import com.finova.integration.google.dto.GoogleSendEmailRequest;

public interface GoogleGmailService {
    void sendEmail(Long userId, GoogleSendEmailRequest request);
    void sendMonthlySummaryEmail(Long userId);
    void sendBudgetAlertEmail(Long userId, String category, Double spent, Double limit);
}
