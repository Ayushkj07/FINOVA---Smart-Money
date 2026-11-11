package com.finova.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class ResendOtpRequest {

    @NotBlank(message = "Challenge ID is required")
    private String challengeId;

    public ResendOtpRequest() {}

    public ResendOtpRequest(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }
}
