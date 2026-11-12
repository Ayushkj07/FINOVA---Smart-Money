package com.finova.service;

public interface EmailService {
    void sendVerificationOtpEmail(String toEmail, String fullName, String otp, int expiryMinutes);
    void sendPasswordResetEmail(String toEmail, String fullName, String resetToken);
    boolean isEmailConfigured();
}
