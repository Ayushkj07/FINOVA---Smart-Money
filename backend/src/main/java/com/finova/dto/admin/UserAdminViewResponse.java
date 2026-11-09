package com.finova.dto.admin;

import java.time.Instant;
import java.util.List;

public class UserAdminViewResponse {


    private Long id;
    private String email;
    private String fullName;
    private String collegeName;
    private String studentStatus;
    private String currency;
    private Boolean isActive;
    private Boolean isVerified;
    private List<String> roles;
    private Instant createdAt;


    public UserAdminViewResponse() {}

    public UserAdminViewResponse(Long id, String email, String fullName, String collegeName, String studentStatus, String currency, Boolean isActive, Boolean isVerified, List<String> roles, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.collegeName = collegeName;
        this.studentStatus = studentStatus;
        this.currency = currency;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCollegeName() {
        return this.collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getStudentStatus() {
        return this.studentStatus;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Boolean isIsActive() {
        return this.isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsVerified() {
        return this.isVerified;
    }

    public Boolean isIsVerified() {
        return this.isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static UserAdminViewResponseBuilder builder() {
        return new UserAdminViewResponseBuilder();
    }

    public static class UserAdminViewResponseBuilder {
        private Long id;
        private String email;
        private String fullName;
        private String collegeName;
        private String studentStatus;
        private String currency;
        private Boolean isActive;
        private Boolean isVerified;
        private List<String> roles;
        private Instant createdAt;

        public UserAdminViewResponseBuilder() {}

        public UserAdminViewResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserAdminViewResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserAdminViewResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserAdminViewResponseBuilder collegeName(String collegeName) {
            this.collegeName = collegeName;
            return this;
        }

        public UserAdminViewResponseBuilder studentStatus(String studentStatus) {
            this.studentStatus = studentStatus;
            return this;
        }

        public UserAdminViewResponseBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public UserAdminViewResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserAdminViewResponseBuilder isVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public UserAdminViewResponseBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public UserAdminViewResponseBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserAdminViewResponse build() {
            UserAdminViewResponse instance = new UserAdminViewResponse();
            instance.setId(this.id);
            instance.setEmail(this.email);
            instance.setFullName(this.fullName);
            instance.setCollegeName(this.collegeName);
            instance.setStudentStatus(this.studentStatus);
            instance.setCurrency(this.currency);
            instance.setIsActive(this.isActive);
            instance.setIsVerified(this.isVerified);
            instance.setRoles(this.roles);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
