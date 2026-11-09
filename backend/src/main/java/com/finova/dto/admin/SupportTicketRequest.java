package com.finova.dto.admin;

import jakarta.validation.constraints.NotBlank;
public class SupportTicketRequest {



    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message is required")
    private String message;

    private String priority = "MEDIUM";


    public SupportTicketRequest() {}

    public SupportTicketRequest(String subject, String message, String priority) {
        this.subject = subject;
        this.message = message;
        this.priority = priority;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public static SupportTicketRequestBuilder builder() {
        return new SupportTicketRequestBuilder();
    }

    public static class SupportTicketRequestBuilder {
        private String subject;
        private String message;
        private String priority;

        public SupportTicketRequestBuilder() {}

        public SupportTicketRequestBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public SupportTicketRequestBuilder message(String message) {
            this.message = message;
            return this;
        }

        public SupportTicketRequestBuilder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public SupportTicketRequest build() {
            SupportTicketRequest instance = new SupportTicketRequest();
            instance.setSubject(this.subject);
            instance.setMessage(this.message);
            instance.setPriority(this.priority);
            return instance;
        }
    }
}
