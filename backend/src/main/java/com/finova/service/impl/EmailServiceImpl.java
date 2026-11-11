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

    @Value("${app.mail.from-name:FINOVA Security}")
    private String fromName;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    @Override
    public void sendLoginOtpEmail(String toEmail, String fullName, String otp, int expiryMinutes) {
        if (mailHost == null || mailHost.isBlank() || mailHost.contains("YOUR_") || mailHost.startsWith("${")) {
            throw new BadRequestException(
                "Email service is not configured. Please set SPRING_MAIL_HOST, SPRING_MAIL_USERNAME, and SPRING_MAIL_PASSWORD in backend environment variables to send verification OTP emails."
            );
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BadRequestException(
                "SMTP mail sender is unavailable. Please verify your spring.mail.* settings."
            );
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom(new InternetAddress(fromEmail, fromName));
            helper.setTo(toEmail);
            helper.setSubject("Your FINOVA Verification Code: " + otp);

            String htmlBody = buildOtpEmailHtml(fullName != null ? fullName : "FINOVA Student", otp, expiryMinutes);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("Successfully sent 6-digit verification OTP email to {}", toEmail);
        } catch (BadRequestException bre) {
            throw bre;
        } catch (Exception ex) {
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage());
            throw new BadRequestException("Failed to deliver verification email to " + toEmail + ": " + ex.getMessage() + ". Please verify SMTP server settings.");
        }
    }

    private String buildOtpEmailHtml(String recipientName, String otp, int expiryMinutes) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'><title>FINOVA Login Verification</title></head>"
                + "<body style='font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif; background-color: #F8F9FA; margin: 0; padding: 32px 16px;'>"
                + "  <table width='100%' border='0' cellspacing='0' cellpadding='0' style='max-width: 520px; margin: 0 auto; background-color: #ffffff; border-radius: 24px; border: 1px solid #E2E8F0; box-shadow: 0 4px 12px rgba(0,0,0,0.03); overflow: hidden;'>"
                + "    <tr>"
                + "      <td style='padding: 36px 36px 20px 36px; text-align: center; border-bottom: 1px solid #F1F5F9;'>"
                + "        <div style='display: inline-block; width: 44px; height: 44px; line-height: 44px; border-radius: 14px; background-color: #9B87F5; color: #ffffff; font-weight: 900; font-size: 22px; text-align: center;'>F</div>"
                + "        <div style='font-size: 20px; font-weight: 800; color: #1E293B; letter-spacing: -0.5px; margin-top: 10px;'>FINOVA</div>"
                + "        <div style='font-size: 11px; font-weight: 600; color: #64748B; text-transform: uppercase; letter-spacing: 1px;'>Smart Money Management</div>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style='padding: 32px 36px; color: #334155; font-size: 14px; line-height: 1.6;'>"
                + "        <h2 style='font-size: 18px; font-weight: 800; color: #0F172A; margin: 0 0 12px 0;'>Verify Your Identity</h2>"
                + "        <p style='margin: 0 0 20px 0;'>Hello <strong>" + escapeHtml(recipientName) + "</strong>,</p>"
                + "        <p style='margin: 0 0 24px 0;'>You recently signed in with Google to access your FINOVA account. Use the 6-digit verification code below to complete your login:</p>"
                + "        <div style='background-color: #FAF5FF; border: 1px solid #E9D5FF; border-radius: 16px; padding: 20px; text-align: center; margin: 0 0 24px 0;'>"
                + "          <span style='font-size: 36px; font-weight: 800; letter-spacing: 10px; color: #7E22CE; font-family: monospace; display: inline-block; padding-left: 10px;'>" + escapeHtml(otp) + "</span>"
                + "        </div>"
                + "        <p style='font-size: 12px; color: #64748B; margin: 0 0 16px 0; text-align: center;'>"
                + "          ⏱ This code will expire in <strong>" + expiryMinutes + " minutes</strong> and can only be used once."
                + "        </p>"
                + "        <div style='background-color: #FEF2F2; border-left: 4px solid #EF4444; border-radius: 8px; padding: 12px 14px; margin-top: 24px; font-size: 12px; color: #991B1B;'>"
                + "          <strong>Security Notice:</strong> If you did not initiate this login request, please secure your Google account immediately. Do not share this code with anyone."
                + "        </div>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style='padding: 20px 36px; background-color: #F8FAFC; text-align: center; border-top: 1px solid #F1F5F9; font-size: 11px; color: #94A3B8;'>"
                + "        &copy; 2026 FINOVA Platform &bull; Student Financial Operating System"
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
}
