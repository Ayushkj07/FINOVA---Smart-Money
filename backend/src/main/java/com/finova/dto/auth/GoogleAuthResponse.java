package com.finova.dto.auth;

public class GoogleAuthResponse {
    private boolean challengeRequired = true;
    private String challengeId;
    private String email;
    private String maskedEmail;
    private long expiresInSeconds = 300;
    private long resendCooldownSeconds = 60;
    private String message;

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
}
