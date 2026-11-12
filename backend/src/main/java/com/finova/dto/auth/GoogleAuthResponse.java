package com.finova.dto.auth;

import java.util.List;

public class GoogleAuthResponse {
    private boolean challengeRequired = false;
    private String challengeId;
    private String email;
    private String maskedEmail;
    private long expiresInSeconds = 300;
    private long resendCooldownSeconds = 60;
    private String message;

    // Direct authenticated FINOVA session attributes
    private String token;
    private String tokenType = "Bearer";
    private String refreshToken;
    private Long id;
    private String fullName;
    private List<String> roles;
    private String collegeName;
    private String currency;
    private String studentStatus;
    private String profilePictureUrl;

    public GoogleAuthResponse() {}

    public GoogleAuthResponse(boolean challengeRequired, String challengeId, String email, String maskedEmail, long expiresInSeconds, long resendCooldownSeconds, String message) {
        this.challengeRequired = challengeRequired;
        this.challengeId = challengeId;
        this.email = email;
        this.maskedEmail = maskedEmail;
        this.expiresInSeconds = expiresInSeconds;
        this.resendCooldownSeconds = resendCooldownSeconds;
        this.message = message;
    }

    public boolean isChallengeRequired() { return challengeRequired; }
    public void setChallengeRequired(boolean challengeRequired) { this.challengeRequired = challengeRequired; }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMaskedEmail() { return maskedEmail; }
    public void setMaskedEmail(String maskedEmail) { this.maskedEmail = maskedEmail; }

    public long getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }

    public long getResendCooldownSeconds() { return resendCooldownSeconds; }
    public void setResendCooldownSeconds(long resendCooldownSeconds) { this.resendCooldownSeconds = resendCooldownSeconds; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStudentStatus() { return studentStatus; }
    public void setStudentStatus(String studentStatus) { this.studentStatus = studentStatus; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
