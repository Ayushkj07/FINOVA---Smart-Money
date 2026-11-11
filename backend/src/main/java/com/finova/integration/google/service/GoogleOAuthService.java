package com.finova.integration.google.service;

import com.finova.dto.auth.GoogleAuthResponse;
import com.finova.integration.google.dto.GoogleAuthRequest;
import com.finova.integration.google.dto.GoogleConnectResponse;
import com.finova.integration.google.dto.GoogleIntegrationStatusResponse;

public interface GoogleOAuthService {
    GoogleConnectResponse getAuthorizationUrl(Long userId, String serviceName);
    void handleCallback(Long userId, String code, String state, String serviceName);
    void disconnectService(Long userId, String serviceName);
    void revokeAllPermissions(Long userId);
    String getValidAccessToken(Long userId);
    GoogleIntegrationStatusResponse getIntegrationStatus(Long userId);
    GoogleAuthResponse authenticateWithGoogle(GoogleAuthRequest request);
}
