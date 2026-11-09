package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_profiles")
public class UserProfile {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "college_name", length = 150)
    private String collegeName;

    @Column(name = "student_status", length = 50)
    private String studentStatus = "ACTIVE_STUDENT";

    @Column(length = 10)
    private String currency = "INR";

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "monthly_allowance", precision = 12, scale = 2)
    private BigDecimal monthlyAllowance = BigDecimal.ZERO;

    @Column(name = "financial_goal_summary", length = 500)
    private String financialGoalSummary;

    @Column(length = 50)
    private String timezone = "Asia/Kolkata";

    @Column(name = "notification_email_enabled")
    private Boolean notificationEmailEnabled = true;

    @Column(name = "notification_in_app_enabled")
    private Boolean notificationInAppEnabled = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();


    public UserProfile() {}

    public UserProfile(Long id, User user, String collegeName, String studentStatus, String currency, String avatarUrl, String phoneNumber, BigDecimal monthlyAllowance, String financialGoalSummary, String timezone, Boolean notificationEmailEnabled, Boolean notificationInAppEnabled, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.collegeName = collegeName;
        this.studentStatus = studentStatus;
        this.currency = currency;
        this.avatarUrl = avatarUrl;
        this.phoneNumber = phoneNumber;
        this.monthlyAllowance = monthlyAllowance;
        this.financialGoalSummary = financialGoalSummary;
        this.timezone = timezone;
        this.notificationEmailEnabled = notificationEmailEnabled;
        this.notificationInAppEnabled = notificationInAppEnabled;
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

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getMonthlyAllowance() {
        return this.monthlyAllowance;
    }

    public void setMonthlyAllowance(BigDecimal monthlyAllowance) {
        this.monthlyAllowance = monthlyAllowance;
    }

    public String getFinancialGoalSummary() {
        return this.financialGoalSummary;
    }

    public void setFinancialGoalSummary(String financialGoalSummary) {
        this.financialGoalSummary = financialGoalSummary;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getNotificationEmailEnabled() {
        return this.notificationEmailEnabled;
    }

    public Boolean isNotificationEmailEnabled() {
        return this.notificationEmailEnabled;
    }

    public void setNotificationEmailEnabled(Boolean notificationEmailEnabled) {
        this.notificationEmailEnabled = notificationEmailEnabled;
    }

    public Boolean getNotificationInAppEnabled() {
        return this.notificationInAppEnabled;
    }

    public Boolean isNotificationInAppEnabled() {
        return this.notificationInAppEnabled;
    }

    public void setNotificationInAppEnabled(Boolean notificationInAppEnabled) {
        this.notificationInAppEnabled = notificationInAppEnabled;
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

    public static UserProfileBuilder builder() {
        return new UserProfileBuilder();
    }

    public static class UserProfileBuilder {
        private Long id;
        private User user;
        private String collegeName;
        private String studentStatus;
        private String currency;
        private String avatarUrl;
        private String phoneNumber;
        private BigDecimal monthlyAllowance;
        private String financialGoalSummary;
        private String timezone;
        private Boolean notificationEmailEnabled;
        private Boolean notificationInAppEnabled;
        private Instant createdAt;
        private Instant updatedAt;

        public UserProfileBuilder() {}

        public UserProfileBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserProfileBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserProfileBuilder collegeName(String collegeName) {
            this.collegeName = collegeName;
            return this;
        }

        public UserProfileBuilder studentStatus(String studentStatus) {
            this.studentStatus = studentStatus;
            return this;
        }

        public UserProfileBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public UserProfileBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserProfileBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserProfileBuilder monthlyAllowance(BigDecimal monthlyAllowance) {
            this.monthlyAllowance = monthlyAllowance;
            return this;
        }

        public UserProfileBuilder financialGoalSummary(String financialGoalSummary) {
            this.financialGoalSummary = financialGoalSummary;
            return this;
        }

        public UserProfileBuilder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public UserProfileBuilder notificationEmailEnabled(Boolean notificationEmailEnabled) {
            this.notificationEmailEnabled = notificationEmailEnabled;
            return this;
        }

        public UserProfileBuilder notificationInAppEnabled(Boolean notificationInAppEnabled) {
            this.notificationInAppEnabled = notificationInAppEnabled;
            return this;
        }

        public UserProfileBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserProfileBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserProfile build() {
            UserProfile instance = new UserProfile();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setCollegeName(this.collegeName);
            instance.setStudentStatus(this.studentStatus);
            instance.setCurrency(this.currency);
            instance.setAvatarUrl(this.avatarUrl);
            instance.setPhoneNumber(this.phoneNumber);
            instance.setMonthlyAllowance(this.monthlyAllowance);
            instance.setFinancialGoalSummary(this.financialGoalSummary);
            instance.setTimezone(this.timezone);
            instance.setNotificationEmailEnabled(this.notificationEmailEnabled);
            instance.setNotificationInAppEnabled(this.notificationInAppEnabled);
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
