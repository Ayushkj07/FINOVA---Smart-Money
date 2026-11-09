package com.finova.dto.auth;

import java.math.BigDecimal;
import java.util.List;

public class UserProfileResponse {


    private Long id;
    private String email;
    private String fullName;
    private List<String> roles;
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


    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String email, String fullName, List<String> roles, String collegeName, String studentStatus, String currency, String avatarUrl, String phoneNumber, BigDecimal monthlyAllowance, String financialGoalSummary, String timezone, Boolean notificationEmailEnabled, Boolean notificationInAppEnabled) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
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

    public List<String> getRoles() {
        return this.roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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

    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
    }

    public static class UserProfileResponseBuilder {
        private Long id;
        private String email;
        private String fullName;
        private List<String> roles;
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

        public UserProfileResponseBuilder() {}

        public UserProfileResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserProfileResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserProfileResponseBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public UserProfileResponseBuilder collegeName(String collegeName) {
            this.collegeName = collegeName;
            return this;
        }

        public UserProfileResponseBuilder studentStatus(String studentStatus) {
            this.studentStatus = studentStatus;
            return this;
        }

        public UserProfileResponseBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public UserProfileResponseBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserProfileResponseBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserProfileResponseBuilder monthlyAllowance(BigDecimal monthlyAllowance) {
            this.monthlyAllowance = monthlyAllowance;
            return this;
        }

        public UserProfileResponseBuilder financialGoalSummary(String financialGoalSummary) {
            this.financialGoalSummary = financialGoalSummary;
            return this;
        }

        public UserProfileResponseBuilder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public UserProfileResponseBuilder notificationEmailEnabled(Boolean notificationEmailEnabled) {
            this.notificationEmailEnabled = notificationEmailEnabled;
            return this;
        }

        public UserProfileResponseBuilder notificationInAppEnabled(Boolean notificationInAppEnabled) {
            this.notificationInAppEnabled = notificationInAppEnabled;
            return this;
        }

        public UserProfileResponse build() {
            UserProfileResponse instance = new UserProfileResponse();
            instance.setId(this.id);
            instance.setEmail(this.email);
            instance.setFullName(this.fullName);
            instance.setRoles(this.roles);
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
            return instance;
        }
    }
}
