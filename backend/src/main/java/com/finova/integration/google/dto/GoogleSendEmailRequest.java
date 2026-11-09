package com.finova.integration.google.dto;

public class GoogleSendEmailRequest {
    private String toEmail;
    private String subject;
    private String emailType; // MONTHLY_SUMMARY, BUDGET_WARNING, SAVINGS_UPDATE, SUBSCRIPTION_REMINDER, REPAYMENT_REMINDER, CUSTOM
    private String customMessage;

    public GoogleSendEmailRequest() {}

    public GoogleSendEmailRequest(String toEmail, String subject, String emailType, String customMessage) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.emailType = emailType;
        this.customMessage = customMessage;
    }

    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getEmailType() { return emailType; }
    public void setEmailType(String emailType) { this.emailType = emailType; }

    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) { this.customMessage = customMessage; }
}
