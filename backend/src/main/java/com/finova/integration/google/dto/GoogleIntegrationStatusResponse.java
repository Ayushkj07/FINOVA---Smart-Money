package com.finova.integration.google.dto;

import java.time.Instant;
import java.util.List;

public class GoogleIntegrationStatusResponse {
    private boolean hasGoogleAccountLinked;
    private String googleEmail;
    private String googleName;
    private String profilePicture;
    private Instant linkedAt;
    private List<GoogleServiceStatusDto> services;

    public GoogleIntegrationStatusResponse() {}

    public GoogleIntegrationStatusResponse(boolean hasGoogleAccountLinked, String googleEmail, String googleName, String profilePicture, Instant linkedAt, List<GoogleServiceStatusDto> services) {
        this.hasGoogleAccountLinked = hasGoogleAccountLinked;
        this.googleEmail = googleEmail;
        this.googleName = googleName;
        this.profilePicture = profilePicture;
        this.linkedAt = linkedAt;
        this.services = services;
    }

    public boolean isHasGoogleAccountLinked() { return hasGoogleAccountLinked; }
    public void setHasGoogleAccountLinked(boolean hasGoogleAccountLinked) { this.hasGoogleAccountLinked = hasGoogleAccountLinked; }

    public String getGoogleEmail() { return googleEmail; }
    public void setGoogleEmail(String googleEmail) { this.googleEmail = googleEmail; }

    public String getGoogleName() { return googleName; }
    public void setGoogleName(String googleName) { this.googleName = googleName; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public Instant getLinkedAt() { return linkedAt; }
    public void setLinkedAt(Instant linkedAt) { this.linkedAt = linkedAt; }

    public List<GoogleServiceStatusDto> getServices() { return services; }
    public void setServices(List<GoogleServiceStatusDto> services) { this.services = services; }
}
