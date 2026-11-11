package com.finova.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyOtpRequest {

    @NotBlank(message = "Challenge ID is required")
    private String challengeId;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Verification code must be exactly 6 digits")
    private String otp;

    public VerifyOtpRequest() {}

    public VerifyOtpRequest(String challengeId, String otp) {
        this.challengeId = challengeId;
        this.otp = otp;
    }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
