package com.finova.dto.auth;

import java.util.List;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private String refreshToken;
    private Long id;
    private String email;
    private String fullName;
    private List<String> roles;
    private String collegeName;
    private String currency;
    private String studentStatus;
    private String profilePictureUrl;

    // Challenge & OTP verification attributes
    private boolean otpRequired = false;
    private String challengeId;
    private String maskedEmail;
    private Long expiresInSeconds;
    private Long resendCooldownSeconds;

    public AuthResponse() {}

    public AuthResponse(String token, String tokenType, String refreshToken, Long id, String email, String fullName, List<String> roles, String collegeName, String currency, String studentStatus, String profilePictureUrl) {
        this.token = token;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
        this.collegeName = collegeName;
        this.currency = currency;
        this.studentStatus = studentStatus;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStudentStatus() {
        return this.studentStatus;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean isOtpRequired() {
        return otpRequired;
    }

    public void setOtpRequired(boolean otpRequired) {
        this.otpRequired = otpRequired;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getMaskedEmail() {
        return maskedEmail;
    }

    public void setMaskedEmail(String maskedEmail) {
        this.maskedEmail = maskedEmail;
    }

    public Long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(Long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public Long getResendCooldownSeconds() {
        return resendCooldownSeconds;
    }

    public void setResendCooldownSeconds(Long resendCooldownSeconds) {
        this.resendCooldownSeconds = resendCooldownSeconds;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String tokenType = "Bearer";
        private String refreshToken;
        private Long id;
        private String email;
        private String fullName;
        private List<String> roles;
        private String collegeName;
        private String currency;
        private String studentStatus;
        private String profilePictureUrl;
        private boolean otpRequired = false;
        private String challengeId;
        private String maskedEmail;
        private Long expiresInSeconds;
        private Long resendCooldownSeconds;

        public AuthResponseBuilder() {}

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public AuthResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AuthResponseBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public AuthResponseBuilder collegeName(String collegeName) {
            this.collegeName = collegeName;
            return this;
        }

        public AuthResponseBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public AuthResponseBuilder studentStatus(String studentStatus) {
            this.studentStatus = studentStatus;
            return this;
        }

        public AuthResponseBuilder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public AuthResponseBuilder otpRequired(boolean otpRequired) {
            this.otpRequired = otpRequired;
            return this;
        }

        public AuthResponseBuilder challengeId(String challengeId) {
            this.challengeId = challengeId;
            return this;
        }

        public AuthResponseBuilder maskedEmail(String maskedEmail) {
            this.maskedEmail = maskedEmail;
            return this;
        }

        public AuthResponseBuilder expiresInSeconds(Long expiresInSeconds) {
            this.expiresInSeconds = expiresInSeconds;
            return this;
        }

        public AuthResponseBuilder resendCooldownSeconds(Long resendCooldownSeconds) {
            this.resendCooldownSeconds = resendCooldownSeconds;
            return this;
        }

        public AuthResponse build() {
            AuthResponse instance = new AuthResponse();
            instance.setToken(this.token);
            instance.setTokenType(this.tokenType);
            instance.setRefreshToken(this.refreshToken);
            instance.setId(this.id);
            instance.setEmail(this.email);
            instance.setFullName(this.fullName);
            instance.setRoles(this.roles);
            instance.setCollegeName(this.collegeName);
            instance.setCurrency(this.currency);
            instance.setStudentStatus(this.studentStatus);
            instance.setProfilePictureUrl(this.profilePictureUrl);
            instance.setOtpRequired(this.otpRequired);
            instance.setChallengeId(this.challengeId);
            instance.setMaskedEmail(this.maskedEmail);
            instance.setExpiresInSeconds(this.expiresInSeconds);
            instance.setResendCooldownSeconds(this.resendCooldownSeconds);
            return instance;
        }
    }
}
