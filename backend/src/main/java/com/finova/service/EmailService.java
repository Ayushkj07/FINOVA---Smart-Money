package com.finova.service;

public interface EmailService {
    void sendLoginOtpEmail(String toEmail, String fullName, String otp, int expiryMinutes);
}
