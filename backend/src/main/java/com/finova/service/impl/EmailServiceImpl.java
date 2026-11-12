package com.finova.service.impl;

import com.finova.exception.BadRequestException;
import com.finova.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${app.mail.from:noreply@finova.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:FINOVA Team}")
    private String fromName;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    @Override
    public boolean isEmailConfigured() {
        return mailHost != null && !mailHost.isBlank() && !mailHost.contains("YOUR_") && !mailHost.startsWith("${");
    }

    @Override
    public void sendVerificationOtpEmail(String toEmail, String fullName, String otp, int expiryMinutes) {
        if (!isEmailConfigured()) {
            throw new BadRequestException(
                "Email delivery service is currently not configured on this server. " +
                "Please configure MAIL_HOST, MAIL_USERNAME, and MAIL_PASSWORD in the environment variables."
            );
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BadRequestException("SMTP JavaMailSender bean is unavailable. Please check mail server settings.");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom(new InternetAddress(fromEmail, fromName));
            helper.setTo(toEmail);
            helper.setSubject("FINOVA – Your Email Verification Code");

            String recipient = (fullName != null && !fullName.isBlank()) ? fullName.trim() : "Student";
            String plainText = "Hello " + recipient + ",\n\n"
                    + "Welcome to FINOVA!\n\n"
                    + "Your email verification code is:\n\n"
                    + otp + "\n\n"
                    + "This code expires in " + expiryMinutes + " minutes.\n\n"
                    + "If you did not request this verification, please ignore this email.\n\n"
                    + "Regards,\n"
                    + "FINOVA Team\n"
                    + "Smart Money. Smarter Future.";

            String htmlBody = buildOtpEmailHtml(recipient, otp, expiryMinutes);
            helper.setText(plainText, htmlBody);

            mailSender.send(message);
            log.info("Successfully dispatched verification OTP email to {}", maskEmail(toEmail));
        } catch (BadRequestException bre) {
            throw bre;
        } catch (Exception ex) {
            log.error("Failed to deliver verification OTP email to {}: {}", maskEmail(toEmail), ex.getMessage());
            throw new BadRequestException("Failed to deliver verification email to " + toEmail + ". " + ex.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetToken) {
        if (!isEmailConfigured()) {
            log.warn("Cannot send password reset email: SMTP is not configured.");
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom(new InternetAddress(fromEmail, fromName));
            helper.setTo(toEmail);
            helper.setSubject("FINOVA – Password Reset Request");

            String recipient = (fullName != null && !fullName.isBlank()) ? fullName.trim() : "Student";
            String resetUrl = "http://localhost:8080/reset-password?token=" + resetToken;

            String plainText = "Hello " + recipient + ",\n\n"
                    + "You recently requested to reset the password for your FINOVA account.\n"
                    + "Please use the following link to choose a new password:\n\n"
                    + resetUrl + "\n\n"
                    + "This link will expire in 1 hour.\n\n"
                    + "If you did not request a password reset, please ignore this email.\n\n"
                    + "Regards,\n"
                    + "FINOVA Team\n"
                    + "Smart Money. Smarter Future.";

            helper.setText(plainText);
            mailSender.send(message);
            log.info("Dispatched password reset email to {}", maskEmail(toEmail));
        } catch (Exception ex) {
            log.error("Failed to send password reset email to {}: {}", maskEmail(toEmail), ex.getMessage());
        }
    }

    private String buildOtpEmailHtml(String recipientName, String otp, int expiryMinutes) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'><title>FINOVA Email Verification</title></head>"
                + "<body style='font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif; background-color: #F8F9FA; margin: 0; padding: 32px 16px;'>"
                + "  <table width='100%' border='0' cellspacing='0' cellpadding='0' style='max-width: 520px; margin: 0 auto; background-color: #ffffff; border-radius: 24px; border: 1px solid #E2E8F0; box-shadow: 0 4px 12px rgba(0,0,0,0.03); overflow: hidden;'>"
                + "    <tr>"
                + "      <td style='padding: 36px 36px 20px 36px; text-align: center; border-bottom: 1px solid #F1F5F9;'>"
                + "        <div style='display: inline-block; width: 44px; height: 44px; line-height: 44px; border-radius: 14px; background-color: #9B87F5; color: #ffffff; font-weight: 900; font-size: 22px; text-align: center;'>F</div>"
                + "        <div style='font-size: 20px; font-weight: 800; color: #1E293B; letter-spacing: -0.5px; margin-top: 10px;'>FINOVA</div>"
                + "        <div style='font-size: 11px; font-weight: 600; color: #64748B; text-transform: uppercase; letter-spacing: 1px;'>Smart Money. Smarter Future.</div>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style='padding: 32px 36px; color: #334155; font-size: 14px; line-height: 1.6;'>"
                + "        <h2 style='font-size: 18px; font-weight: 800; color: #0F172A; margin: 0 0 12px 0;'>Verify Your Email Address</h2>"
                + "        <p style='margin: 0 0 16px 0;'>Hello <strong>" + escapeHtml(recipientName) + "</strong>,</p>"
                + "        <p style='margin: 0 0 20px 0;'>Welcome to FINOVA!</p>"
                + "        <p style='margin: 0 0 12px 0;'>Your email verification code is:</p>"
                + "        <div style='background-color: #FAF5FF; border: 1px solid #E9D5FF; border-radius: 16px; padding: 20px; text-align: center; margin: 0 0 20px 0;'>"
                + "          <span style='font-size: 36px; font-weight: 800; letter-spacing: 10px; color: #7E22CE; font-family: monospace; display: inline-block; padding-left: 10px;'>" + escapeHtml(otp) + "</span>"
                + "        </div>"
                + "        <p style='font-size: 13px; color: #64748B; margin: 0 0 20px 0; text-align: center;'>"
                + "          ⏱ This code expires in <strong>" + expiryMinutes + " minutes</strong>."
                + "        </p>"
                + "        <p style='font-size: 12px; color: #94A3B8; margin: 0 0 24px 0;'>"
                + "          If you did not request this verification, please ignore this email."
                + "        </p>"
                + "        <div style='border-top: 1px solid #F1F5F9; padding-top: 16px; font-size: 13px; color: #475569;'>"
                + "          Regards,<br/><strong>FINOVA Team</strong><br/><span style='color: #64748B; font-size: 11px;'>Smart Money. Smarter Future.</span>"
                + "        </div>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style='padding: 20px 36px; background-color: #F8FAFC; text-align: center; border-top: 1px solid #F1F5F9; font-size: 11px; color: #94A3B8;'>"
                + "        &copy; 2026 FINOVA Platform &bull; Smart Money Management"
                + "      </td>"
                + "    </tr>"
                + "  </table>"
                + "</body>"
                + "</html>";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "email";
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) {
            return name.charAt(0) + "***@" + domain;
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + "@" + domain;
    }
}
