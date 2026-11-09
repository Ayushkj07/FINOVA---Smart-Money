package com.finova.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(length = 30)
    private String status = "OPEN"; // OPEN, IN_PROGRESS, RESOLVED, CLOSED

    @Column(length = 30)
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    @Column(name = "admin_response", length = 2000)
    private String adminResponse;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public SupportTicket() {}

    public SupportTicket(Long id, User user, String subject, String message, String status, String priority, String adminResponse, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
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

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public static SupportTicketBuilder builder() {
        return new SupportTicketBuilder();
    }

    public static class SupportTicketBuilder {
        private Long id;
        private User user;
        private String subject;
        private String message;
        private String status;
        private String priority;
        private String adminResponse;
        private Instant createdAt;
        private Instant updatedAt;

        public SupportTicketBuilder() {}

        public SupportTicketBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SupportTicketBuilder user(User user) {
            this.user = user;
            return this;
        }

        public SupportTicketBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public SupportTicketBuilder message(String message) {
            this.message = message;
            return this;
        }

        public SupportTicketBuilder status(String status) {
            this.status = status;
            return this;
        }

        public SupportTicketBuilder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public SupportTicketBuilder adminResponse(String adminResponse) {
            this.adminResponse = adminResponse;
            return this;
        }

        public SupportTicketBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SupportTicketBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public SupportTicket build() {
            SupportTicket instance = new SupportTicket();
            instance.setId(this.id);
            instance.setUser(this.user);
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

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.Instant.now();
    }
}
