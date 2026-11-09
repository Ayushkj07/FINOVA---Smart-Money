package com.finova.dto.admin;

import java.time.Instant;

public class SupportTicketResponse {


    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private String subject;
    private String message;
    private String status;
    private String priority;
    private String adminResponse;
    private Instant createdAt;
    private Instant updatedAt;


    public SupportTicketResponse() {}

    public SupportTicketResponse(Long id, Long userId, String userEmail, String userName, String subject, String message, String status, String priority, String adminResponse, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.priority = priority;
        this.adminResponse = adminResponse;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAdminResponse() {
        return this.adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static SupportTicketResponseBuilder builder() {
        return new SupportTicketResponseBuilder();
    }

    public static class SupportTicketResponseBuilder {
        private Long id;
        private Long userId;
        private String userEmail;
        private String userName;
        private String subject;
        private String message;
        private String status;
        private String priority;
        private String adminResponse;
        private Instant createdAt;
        private Instant updatedAt;

        public SupportTicketResponseBuilder() {}

        public SupportTicketResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SupportTicketResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public SupportTicketResponseBuilder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public SupportTicketResponseBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public SupportTicketResponseBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public SupportTicketResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public SupportTicketResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public SupportTicketResponseBuilder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public SupportTicketResponseBuilder adminResponse(String adminResponse) {
            this.adminResponse = adminResponse;
            return this;
        }

        public SupportTicketResponseBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SupportTicketResponseBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public SupportTicketResponse build() {
            SupportTicketResponse instance = new SupportTicketResponse();
            instance.setId(this.id);
            instance.setUserId(this.userId);
            instance.setUserEmail(this.userEmail);
            instance.setUserName(this.userName);
            instance.setSubject(this.subject);
            instance.setMessage(this.message);
            instance.setStatus(this.status);
            instance.setPriority(this.priority);
            instance.setAdminResponse(this.adminResponse);
            instance.setCreatedAt(this.createdAt);
            instance.setUpdatedAt(this.updatedAt);
            return instance;
        }
    }
}
