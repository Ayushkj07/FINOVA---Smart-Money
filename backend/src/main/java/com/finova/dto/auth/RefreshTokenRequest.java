package com.finova.dto.auth;

import jakarta.validation.constraints.NotBlank;
public class RefreshTokenRequest {


    @NotBlank(message = "Refresh token is required")
    private String refreshToken;


    public RefreshTokenRequest() {}

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static RefreshTokenRequestBuilder builder() {
        return new RefreshTokenRequestBuilder();
    }

    public static class RefreshTokenRequestBuilder {
        private String refreshToken;

        public RefreshTokenRequestBuilder() {}

        public RefreshTokenRequestBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public RefreshTokenRequest build() {
            RefreshTokenRequest instance = new RefreshTokenRequest();
            instance.setRefreshToken(this.refreshToken);
            return instance;
        }
    }
}
