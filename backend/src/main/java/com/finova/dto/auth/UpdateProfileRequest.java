package com.finova.dto.auth;

import java.math.BigDecimal;

public class UpdateProfileRequest {


    private String fullName;
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


    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String fullName, String collegeName, String studentStatus, String currency, String avatarUrl, String phoneNumber, BigDecimal monthlyAllowance, String financialGoalSummary, String timezone, Boolean notificationEmailEnabled, Boolean notificationInAppEnabled) {
        this.fullName = fullName;
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

    public static UpdateProfileRequestBuilder builder() {
        return new UpdateProfileRequestBuilder();
    }

    public static class UpdateProfileRequestBuilder {
        private String fullName;
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

        public UpdateProfileRequestBuilder() {}

        public UpdateProfileRequestBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UpdateProfileRequestBuilder collegeName(String collegeName) {
            this.collegeName = collegeName;
            return this;
        }

        public UpdateProfileRequestBuilder studentStatus(String studentStatus) {
            this.studentStatus = studentStatus;
            return this;
        }

        public UpdateProfileRequestBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public UpdateProfileRequestBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UpdateProfileRequestBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UpdateProfileRequestBuilder monthlyAllowance(BigDecimal monthlyAllowance) {
            this.monthlyAllowance = monthlyAllowance;
            return this;
        }

        public UpdateProfileRequestBuilder financialGoalSummary(String financialGoalSummary) {
            this.financialGoalSummary = financialGoalSummary;
            return this;
        }

        public UpdateProfileRequestBuilder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public UpdateProfileRequestBuilder notificationEmailEnabled(Boolean notificationEmailEnabled) {
            this.notificationEmailEnabled = notificationEmailEnabled;
            return this;
        }

        public UpdateProfileRequestBuilder notificationInAppEnabled(Boolean notificationInAppEnabled) {
            this.notificationInAppEnabled = notificationInAppEnabled;
            return this;
        }

        public UpdateProfileRequest build() {
            UpdateProfileRequest instance = new UpdateProfileRequest();
            instance.setFullName(this.fullName);
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
