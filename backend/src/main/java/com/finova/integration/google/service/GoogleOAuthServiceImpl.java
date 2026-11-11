package com.finova.integration.google.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finova.dto.auth.GoogleAuthResponse;
import com.finova.entity.OAuthAccount;
import com.finova.entity.Role;
import com.finova.entity.enums.RoleType;
import com.finova.entity.User;
import com.finova.entity.UserProfile;
import com.finova.exception.BadRequestException;
import com.finova.exception.ResourceNotFoundException;
import com.finova.integration.google.config.TokenEncryptionService;
import com.finova.integration.google.dto.GoogleAuthRequest;
import com.finova.integration.google.dto.GoogleConnectResponse;
import com.finova.integration.google.dto.GoogleIntegrationStatusResponse;
import com.finova.integration.google.dto.GoogleServiceStatusDto;
import com.finova.integration.google.entity.GoogleAccount;
import com.finova.integration.google.entity.GoogleIntegration;
import com.finova.integration.google.entity.GoogleOAuthToken;
import com.finova.integration.google.entity.GoogleSyncLog;
import com.finova.integration.google.repository.GoogleAccountRepository;
import com.finova.integration.google.repository.GoogleIntegrationRepository;
import com.finova.integration.google.repository.GoogleOAuthTokenRepository;
import com.finova.integration.google.repository.GoogleSyncLogRepository;
import com.finova.repository.OAuthAccountRepository;
import com.finova.repository.RoleRepository;
import com.finova.repository.UserProfileRepository;
import com.finova.repository.UserRepository;
import com.finova.service.OtpChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthServiceImpl.class);

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String GOOGLE_REVOKE_URL = "https://oauth2.googleapis.com/revoke";

    private final GoogleAccountRepository googleAccountRepository;
    private final GoogleIntegrationRepository googleIntegrationRepository;
    private final GoogleOAuthTokenRepository googleOAuthTokenRepository;
    private final GoogleSyncLogRepository googleSyncLogRepository;
    private final OAuthAccountRepository oauthAccountRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final TokenEncryptionService tokenEncryptionService;
    private final OtpChallengeService otpChallengeService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    // Secure in-memory state tracking for CSRF mitigation & matching redirect URIs (TTL 15 mins)
    private final Map<String, OAuthStateInfo> pendingStates = new ConcurrentHashMap<>();

    private static class OAuthStateInfo {
        private final Long userId;
        private final String service;
        private final String redirectUri;
        private final Instant createdAt;

        public OAuthStateInfo(Long userId, String service, String redirectUri, Instant createdAt) {
            this.userId = userId;
            this.service = service;
            this.redirectUri = redirectUri;
            this.createdAt = createdAt;
        }

        public Long getUserId() { return userId; }
        public String getService() { return service; }
        public String getRedirectUri() { return redirectUri; }
        public Instant getCreatedAt() { return createdAt; }
    }

    @Value("${app.google.client-id}")
    private String clientId;

    @Value("${app.google.client-secret}")
    private String clientSecret;

    @Value("${app.google.redirect-uri}")
    private String redirectUri;

    public GoogleOAuthServiceImpl(GoogleAccountRepository googleAccountRepository,
                                 GoogleIntegrationRepository googleIntegrationRepository,
                                 GoogleOAuthTokenRepository googleOAuthTokenRepository,
                                 GoogleSyncLogRepository googleSyncLogRepository,
                                 OAuthAccountRepository oauthAccountRepository,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 UserProfileRepository userProfileRepository,
                                 TokenEncryptionService tokenEncryptionService,
                                 OtpChallengeService otpChallengeService) {
        this.googleAccountRepository = googleAccountRepository;
        this.googleIntegrationRepository = googleIntegrationRepository;
        this.googleOAuthTokenRepository = googleOAuthTokenRepository;
        this.googleSyncLogRepository = googleSyncLogRepository;
        this.oauthAccountRepository = oauthAccountRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.tokenEncryptionService = tokenEncryptionService;
        this.otpChallengeService = otpChallengeService;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
    }

    private String getScopeForService(String serviceName) {
        if (serviceName == null) return "openid email profile";
        return switch (serviceName.toUpperCase()) {
            case "CALENDAR" -> "openid email profile https://www.googleapis.com/auth/calendar.events";
            case "SHEETS" -> "openid email profile https://www.googleapis.com/auth/spreadsheets";
            case "DRIVE" -> "openid email profile https://www.googleapis.com/auth/drive.file";
            case "GMAIL" -> "openid email profile https://www.googleapis.com/auth/gmail.send";
            case "ALL" -> "openid email profile https://www.googleapis.com/auth/calendar.events https://www.googleapis.com/auth/spreadsheets https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/gmail.send";
            default -> "openid email profile";
        };
    }

    private void cleanExpiredStates() {
        Instant cutoff = Instant.now().minus(15, ChronoUnit.MINUTES);
        pendingStates.entrySet().removeIf(entry -> entry.getValue().getCreatedAt().isBefore(cutoff));
    }

    private String resolveRedirectUri(String customRedirectUri) {
        if (customRedirectUri != null && !customRedirectUri.isBlank()) {
            return customRedirectUri.trim();
        }
        if (this.redirectUri != null && !this.redirectUri.isBlank() && !this.redirectUri.startsWith("${")) {
            return this.redirectUri.trim();
        }
        return "http://localhost:8080/settings/connected-apps/callback";
    }

    @Override
    public GoogleConnectResponse getAuthorizationUrl(Long userId, String serviceName) {
        return getAuthorizationUrl(userId, serviceName, null);
    }

    @Override
    public GoogleConnectResponse getAuthorizationUrl(Long userId, String serviceName, String customRedirectUri) {
        if (clientId == null || clientId.isBlank() || clientId.contains("YOUR_") || clientId.startsWith("${")) {
            throw new BadRequestException(
                "Google Cloud OAuth 2.0 is not configured. Please set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET in backend environment variables to enable Google sign-in."
            );
        }

        cleanExpiredStates();

        String service = serviceName != null ? serviceName.toUpperCase() : "AUTH";
        String scope = getScopeForService(service);
        String effectiveRedirectUri = resolveRedirectUri(customRedirectUri);

        String stateToken = UUID.randomUUID().toString().replace("-", "");
        String state = stateToken + "_" + (userId != null ? userId : "guest") + "_" + service;

        pendingStates.put(stateToken, new OAuthStateInfo(userId, service, effectiveRedirectUri, Instant.now()));

        // Use prompt=select_account consent to enforce Google's official account picker
        String authUrl = UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", effectiveRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "select_account consent")
                .queryParam("include_granted_scopes", "true")
                .queryParam("state", state)
                .build().toUriString();

        return new GoogleConnectResponse(authUrl, state, service, scope);
    }

    @Override
    @Transactional
    public void handleCallback(Long userId, String code, String state, String serviceName) {
        handleCallback(userId, code, state, serviceName, null);
    }

    @Override
    @Transactional
    public void handleCallback(Long userId, String code, String state, String serviceName, String customRedirectUri) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (code == null || code.isBlank()) {
            throw new BadRequestException("Authorization code is required to complete Google OAuth.");
        }

        if (clientId == null || clientId.isBlank() || clientId.contains("YOUR_") || clientId.startsWith("${")) {
            throw new BadRequestException("Google Cloud OAuth 2.0 is not configured on the server.");
        }

        // Validate state & retrieve recorded redirectUri
        String effectiveRedirectUri = customRedirectUri;
        if (state != null) {
            String stateToken = state.contains("_") ? state.split("_")[0] : state;
            OAuthStateInfo stateInfo = pendingStates.remove(stateToken);
            if (stateInfo != null && (effectiveRedirectUri == null || effectiveRedirectUri.isBlank())) {
                effectiveRedirectUri = stateInfo.getRedirectUri();
            }
        }
        effectiveRedirectUri = resolveRedirectUri(effectiveRedirectUri);

        String service = serviceName != null ? serviceName.toUpperCase() : "AUTH";
        try {
            // Exchange authorization code for real Google tokens
            Map<String, Object> tokenPayload = exchangeCodeForTokens(code, effectiveRedirectUri);
            String accessToken = (String) tokenPayload.get("access_token");
            String refreshToken = (String) tokenPayload.get("refresh_token");
            Integer expiresIn = (Integer) tokenPayload.get("expires_in");
            String scope = (String) tokenPayload.get("scope");

            // Fetch Google user profile
            Map<String, Object> userInfo = fetchGoogleUserInfo(accessToken);
            String googleUserId = (String) userInfo.get("sub");
            String googleEmail = (String) userInfo.get("email");
            String profilePic = (String) userInfo.get("picture");

            Instant expiryInstant = Instant.now().plusSeconds(expiresIn != null ? expiresIn : 3600);

            // Save or update GoogleAccount
            GoogleAccount account = googleAccountRepository.findByUser(user)
                    .orElse(new GoogleAccount(null, user, googleUserId, googleEmail, profilePic, Instant.now(), Instant.now()));
            account.setGoogleUserId(googleUserId);
            account.setGoogleEmail(googleEmail);
            account.setProfilePicture(profilePic);
            account.setUpdatedAt(Instant.now());
            googleAccountRepository.save(account);

            // Save or update OAuthAccount
            OAuthAccount oauthAccount = oauthAccountRepository.findByUserAndProvider(user, "GOOGLE")
                    .orElse(new OAuthAccount(null, user, "GOOGLE", googleUserId, googleEmail, Instant.now()));
            oauthAccount.setProviderUserId(googleUserId);
            oauthAccount.setProviderEmail(googleEmail);
            oauthAccountRepository.save(oauthAccount);

            // Save or update encrypted GoogleOAuthToken
            String encryptedAccess = tokenEncryptionService.encrypt(accessToken);
            String encryptedRefresh = refreshToken != null ? tokenEncryptionService.encrypt(refreshToken) : null;

            GoogleOAuthToken oauthToken = googleOAuthTokenRepository.findByUser(user)
                    .orElse(new GoogleOAuthToken(null, user, encryptedAccess, encryptedRefresh, expiryInstant, Instant.now(), Instant.now()));

            oauthToken.setEncryptedAccessToken(encryptedAccess);
            if (encryptedRefresh != null) {
                oauthToken.setEncryptedRefreshToken(encryptedRefresh);
            }
            oauthToken.setExpiresAt(expiryInstant);
            oauthToken.setUpdatedAt(Instant.now());
            googleOAuthTokenRepository.save(oauthToken);

            // Connect this specific integration service (and AUTH)
            updateServiceIntegration(user, service, scope != null ? scope : getScopeForService(service));
            if (!"AUTH".equals(service)) {
                updateServiceIntegration(user, "AUTH", "openid email profile");
            }

            // Record sync log
            googleSyncLogRepository.save(new GoogleSyncLog(
                    null, user, service, "SUCCESS", null, "Service " + service + " connected successfully.", Instant.now()
            ));

            log.info("Successfully linked Google service '{}' for user ID {}", service, userId);
        } catch (BadRequestException bre) {
            throw bre;
        } catch (Exception e) {
            log.error("Failed to complete Google OAuth callback for user ID {}: {}", userId, e.getMessage(), e);
            googleSyncLogRepository.save(new GoogleSyncLog(
                    null, user, service, "FAILED", e.getMessage(), "OAuth exchange failed: " + e.getMessage(), Instant.now()
            ));
            throw new BadRequestException("Google integration failed: " + e.getMessage());
        }
    }

    private void updateServiceIntegration(User user, String service, String scope) {
        GoogleIntegration integration = googleIntegrationRepository.findByUserAndServiceName(user, service)
                .orElse(new GoogleIntegration(null, user, service, "CONNECTED", scope, Instant.now(), Instant.now(), Instant.now()));
        integration.setConnectionStatus("CONNECTED");
        integration.setScopes(scope);
        integration.setLastSyncAt(Instant.now());
        integration.setUpdatedAt(Instant.now());
        googleIntegrationRepository.save(integration);
    }

    @Override
    @Transactional
    public void disconnectService(Long userId, String serviceName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String service = serviceName != null ? serviceName.toUpperCase() : "AUTH";
        googleIntegrationRepository.findByUserAndServiceName(user, service).ifPresent(integration -> {
            integration.setConnectionStatus("DISCONNECTED");
            integration.setUpdatedAt(Instant.now());
            googleIntegrationRepository.save(integration);
        });

        if ("AUTH".equals(service) || "ALL".equals(service)) {
            List<GoogleIntegration> allIntegrations = googleIntegrationRepository.findByUser(user);
            for (GoogleIntegration gi : allIntegrations) {
                gi.setConnectionStatus("DISCONNECTED");
                gi.setUpdatedAt(Instant.now());
                googleIntegrationRepository.save(gi);
            }
            revokeAllPermissions(userId);
        }

        googleSyncLogRepository.save(new GoogleSyncLog(
                null, user, service, "SUCCESS", null, "Service " + service + " was disconnected by user.", Instant.now()
        ));
    }

    @Override
    @Transactional
    public void revokeAllPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        googleOAuthTokenRepository.findByUser(user).ifPresent(tokenEntity -> {
            try {
                String accessToken = tokenEncryptionService.decrypt(tokenEntity.getEncryptedAccessToken());
                if (accessToken != null && !accessToken.isBlank()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                    params.add("token", accessToken);
                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
                    restTemplate.postForEntity(GOOGLE_REVOKE_URL, request, String.class);
                }
            } catch (Exception ex) {
                log.warn("Revoke token from Google failed or token expired: {}", ex.getMessage());
            }
            googleOAuthTokenRepository.delete(tokenEntity);
        });

        List<GoogleIntegration> allIntegrations = googleIntegrationRepository.findByUser(user);
        for (GoogleIntegration gi : allIntegrations) {
            gi.setConnectionStatus("DISCONNECTED");
            gi.setUpdatedAt(Instant.now());
            googleIntegrationRepository.save(gi);
        }

        googleAccountRepository.findByUser(user).ifPresent(googleAccountRepository::delete);
        oauthAccountRepository.deleteByUserAndProvider(user, "GOOGLE");

        googleSyncLogRepository.save(new GoogleSyncLog(
                null, user, "AUTH", "REVOKED", null, "All Google authorizations were permanently revoked.", Instant.now()
        ));
    }

    @Override
    public String getValidAccessToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        GoogleOAuthToken token = googleOAuthTokenRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Google account is not connected for this user."));

        if (token.getExpiresAt().isAfter(Instant.now().plusSeconds(60))) {
            return tokenEncryptionService.decrypt(token.getEncryptedAccessToken());
        }

        if (token.getEncryptedRefreshToken() == null) {
            throw new BadRequestException("Google session expired. Please reconnect your Google account.");
        }

        String decryptedRefresh = tokenEncryptionService.decrypt(token.getEncryptedRefreshToken());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("refresh_token", decryptedRefresh);
            body.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String newAccess = root.path("access_token").asText();
                int expiresIn = root.path("expires_in").asInt(3600);

                token.setEncryptedAccessToken(tokenEncryptionService.encrypt(newAccess));
                token.setExpiresAt(Instant.now().plusSeconds(expiresIn));
                token.setUpdatedAt(Instant.now());
                googleOAuthTokenRepository.save(token);

                return newAccess;
            }
        } catch (Exception ex) {
            log.error("Failed to refresh Google access token for user {}: {}", userId, ex.getMessage());
        }

        throw new BadRequestException("Google session expired. Please reconnect your Google account.");
    }

    @Override
    @Transactional(readOnly = true)
    public GoogleIntegrationStatusResponse getIntegrationStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional<GoogleAccount> accountOpt = googleAccountRepository.findByUser(user);
        List<GoogleIntegration> integrations = googleIntegrationRepository.findByUser(user);
        Map<String, GoogleIntegration> map = integrations.stream()
                .collect(Collectors.toMap(GoogleIntegration::getServiceName, gi -> gi, (a, b) -> b));

        List<GoogleServiceStatusDto> serviceDtos = new ArrayList<>();
        String[] supportedServices = {"AUTH", "CALENDAR", "SHEETS", "DRIVE", "GMAIL", "GEMINI", "MAPS"};
        String[] descriptions = {
                "Google Single Sign-On & Account Synchronization",
                "Financial Deadlines & Budget Reminder Synchronization",
                "Automated Financial Reports & Spreadsheet Sync",
                "Receipt Backup & Document Storage",
                "Automated Financial Alerts & Bill Notifications",
                "Personalized Financial Insights via Gemini AI",
                "Transaction Merchant & Campus Geolocation Tagging"
        };

        for (int i = 0; i < supportedServices.length; i++) {
            String s = supportedServices[i];
            GoogleIntegration gi = map.get(s);
            boolean isConnected = gi != null && "CONNECTED".equalsIgnoreCase(gi.getConnectionStatus());
            serviceDtos.add(new GoogleServiceStatusDto(
                    s,
                    isConnected ? "CONNECTED" : "DISCONNECTED",
                    isConnected,
                    gi != null ? gi.getScopes() : getScopeForService(s),
                    gi != null ? gi.getLastSyncAt() : null,
                    descriptions[i]
            ));
        }

        if (accountOpt.isPresent()) {
            GoogleAccount acc = accountOpt.get();
            return new GoogleIntegrationStatusResponse(
                    true,
                    acc.getGoogleEmail(),
                    acc.getUser().getFullName(),
                    acc.getProfilePicture(),
                    acc.getCreatedAt(),
                    serviceDtos
            );
        } else {
            return new GoogleIntegrationStatusResponse(
                    false,
                    null,
                    null,
                    null,
                    null,
                    serviceDtos
            );
        }
    }

    @Override
    @Transactional
    public GoogleAuthResponse authenticateWithGoogle(GoogleAuthRequest request) {
        String googleEmail = null;
        String googleName = null;
        String googleUserId = null;
        String profilePic = null;
        boolean emailVerified = false;

        try {
            if (request.getIdToken() != null && !request.getIdToken().isBlank()) {
                // Verify Google OpenID Connect ID token
                Map<String, Object> payload = verifyGoogleIdToken(request.getIdToken());
                googleUserId = (String) payload.get("sub");
                googleEmail = (String) payload.get("email");
                googleName = (String) payload.get("name");
                profilePic = (String) payload.get("picture");
                emailVerified = Boolean.parseBoolean(String.valueOf(payload.getOrDefault("email_verified", "true")));
            } else if (request.getCode() != null && !request.getCode().isBlank()) {
                if (clientId == null || clientId.isBlank() || clientId.contains("YOUR_") || clientId.startsWith("${")) {
                    throw new BadRequestException("Google Cloud OAuth 2.0 is not configured. Please set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET in backend environment variables.");
                }

                // Determine effective redirect URI from request or state
                String effectiveRedirectUri = request.getRedirectUri();
                if (request.getState() != null) {
                    String stateToken = request.getState().contains("_") ? request.getState().split("_")[0] : request.getState();
                    OAuthStateInfo stateInfo = pendingStates.remove(stateToken);
                    if (stateInfo != null && (effectiveRedirectUri == null || effectiveRedirectUri.isBlank())) {
                        effectiveRedirectUri = stateInfo.getRedirectUri();
                    }
                }
                effectiveRedirectUri = resolveRedirectUri(effectiveRedirectUri);

                // Exchange code for Google tokens using exact matching redirect URI
                Map<String, Object> tokenPayload = exchangeCodeForTokens(request.getCode(), effectiveRedirectUri);
                String accessToken = (String) tokenPayload.get("access_token");
                Map<String, Object> userInfo = fetchGoogleUserInfo(accessToken);
                googleUserId = (String) userInfo.get("sub");
                googleEmail = (String) userInfo.get("email");
                googleName = (String) userInfo.get("name");
                profilePic = (String) userInfo.get("picture");
                emailVerified = Boolean.parseBoolean(String.valueOf(userInfo.getOrDefault("email_verified", "true")));
            } else {
                throw new BadRequestException("Google authorization code or verified id_token is required.");
            }
        } catch (BadRequestException bre) {
            throw bre;
        } catch (Exception ex) {
            log.error("Google authentication token verification failed: {}", ex.getMessage());
            throw new BadRequestException("Invalid Google credentials: " + ex.getMessage());
        }

        if (googleEmail == null || googleEmail.isBlank()) {
            throw new BadRequestException("Unable to retrieve email from Google account.");
        }

        if (!emailVerified) {
            throw new BadRequestException("The email associated with this Google account is not verified by Google.");
        }

        final String finalEmail = googleEmail.toLowerCase().trim();
        final String finalName = (googleName != null && !googleName.isBlank()) ? googleName.trim() : "FINOVA Student";
        final String finalGoogleUserId = googleUserId;
        final String finalProfilePic = (profilePic != null && !profilePic.isBlank())
                ? profilePic
                : "https://lh3.googleusercontent.com/a/default-user";

        // Step 1: Look up in oauth_accounts by (provider = "GOOGLE", provider_user_id = sub)
        Optional<OAuthAccount> oauthAccountOpt = oauthAccountRepository.findByProviderAndProviderUserId("GOOGLE", finalGoogleUserId);
        User user;

        if (oauthAccountOpt.isPresent()) {
            OAuthAccount oauthAccount = oauthAccountOpt.get();
            user = oauthAccount.getUser();
            oauthAccount.setProviderEmail(finalEmail);
            oauthAccountRepository.save(oauthAccount);
            if (user.getFullName() == null || user.getFullName().isBlank() || "FINOVA Student".equals(user.getFullName())) {
                user.setFullName(finalName);
                userRepository.save(user);
            }
        } else {
            // Step 2: Check if existing user by email
            Optional<User> existingUserOpt = userRepository.findByEmail(finalEmail);
            if (existingUserOpt.isPresent()) {
                user = existingUserOpt.get();
                OAuthAccount newOAuth = OAuthAccount.builder()
                        .user(user)
                        .provider("GOOGLE")
                        .providerUserId(finalGoogleUserId)
                        .providerEmail(finalEmail)
                        .createdAt(Instant.now())
                        .build();
                oauthAccountRepository.save(newOAuth);
            } else {
                // Step 3: Create a brand new distinct FINOVA User for this Google account
                Role studentRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                        .orElseGet(() -> roleRepository.save(Role.builder()
                                .name(RoleType.ROLE_STUDENT)
                                .description("Student role")
                                .build()));

                User newUser = User.builder()
                        .email(finalEmail)
                        .fullName(finalName)
                        .password("$2a$10$" + UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""))
                        .isActive(true)
                        .isVerified(false) // Verified upon successful OTP verification!
                        .accountStatus("ACTIVE")
                        .roles(Collections.singleton(studentRole))
                        .build();
                newUser = userRepository.save(newUser);

                UserProfile profile = UserProfile.builder()
                        .user(newUser)
                        .collegeName(request.getCollegeName() != null ? request.getCollegeName().trim() : "University Campus")
                        .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                        .studentStatus("ACTIVE_STUDENT")
                        .avatarUrl(finalProfilePic)
                        .timezone("Asia/Kolkata")
                        .notificationEmailEnabled(true)
                        .notificationInAppEnabled(true)
                        .build();
                userProfileRepository.save(profile);
                newUser.setProfile(profile);

                OAuthAccount newOAuth = OAuthAccount.builder()
                        .user(newUser)
                        .provider("GOOGLE")
                        .providerUserId(finalGoogleUserId)
                        .providerEmail(finalEmail)
                        .createdAt(Instant.now())
                        .build();
                oauthAccountRepository.save(newOAuth);

                user = newUser;
            }
        }

        // Ensure GoogleAccount record is synchronized
        GoogleAccount account = googleAccountRepository.findByUser(user)
                .orElse(new GoogleAccount(null, user, finalGoogleUserId, finalEmail, finalProfilePic, Instant.now(), Instant.now()));
        account.setGoogleUserId(finalGoogleUserId);
        account.setGoogleEmail(finalEmail);
        account.setProfilePicture(finalProfilePic);
        account.setUpdatedAt(Instant.now());
        googleAccountRepository.save(account);

        updateServiceIntegration(user, "AUTH", "openid email profile");

        // REQUIREMENT 2: Google authentication alone must NOT create a session.
        // Issue secure 6-digit OTP challenge to verified Google email!
        return otpChallengeService.createOtpChallenge(user, finalEmail, user.getFullName());
    }

    private Map<String, Object> exchangeCodeForTokens(String code, String effectiveRedirectUri) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", resolveRedirectUri(effectiveRedirectUri));
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BadRequestException("Failed to exchange Google authorization code. Please verify redirect_uri and client credentials in Google Cloud Console.");
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", root.path("access_token").asText());
        if (root.has("refresh_token")) {
            result.put("refresh_token", root.path("refresh_token").asText());
        }
        result.put("expires_in", root.path("expires_in").asInt(3600));
        result.put("scope", root.path("scope").asText());
        return result;
    }

    private Map<String, Object> fetchGoogleUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_URL, HttpMethod.GET, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BadRequestException("Failed to fetch Google profile info.");
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        Map<String, Object> result = new HashMap<>();
        result.put("sub", root.path("sub").asText());
        result.put("email", root.path("email").asText());
        result.put("name", root.path("name").asText());
        result.put("picture", root.path("picture").asText(null));
        result.put("email_verified", root.path("email_verified").asBoolean(true));
        return result;
    }

    private Map<String, Object> verifyGoogleIdToken(String idToken) throws Exception {
        String verifyUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<String> response = restTemplate.getForEntity(verifyUrl, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BadRequestException("Google ID token is invalid or expired.");
        }

        JsonNode root = objectMapper.readTree(response.getBody());

        // Validate Audience matches our Client ID (when configured)
        String aud = root.path("aud").asText();
        if (clientId != null && !clientId.isBlank() && !clientId.contains("YOUR_") && !clientId.startsWith("${") && !clientId.equals(aud)) {
            throw new BadRequestException("Google ID token was not issued for this application client ID.");
        }

        // Validate Issuer
        String iss = root.path("iss").asText();
        if (!"https://accounts.google.com".equals(iss) && !"accounts.google.com".equals(iss)) {
            throw new BadRequestException("Google ID token issuer is invalid.");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sub", root.path("sub").asText());
        result.put("email", root.path("email").asText());
        result.put("name", root.path("name").asText());
        result.put("picture", root.path("picture").asText(null));
        result.put("email_verified", root.path("email_verified").asBoolean(true));
        return result;
    }
}
