package com.finova.integration.google.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finova.dto.auth.AuthResponse;
import com.finova.entity.RefreshToken;
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
import com.finova.repository.RefreshTokenRepository;
import com.finova.repository.RoleRepository;
import com.finova.repository.UserProfileRepository;
import com.finova.repository.UserRepository;
import com.finova.security.JwtTokenProvider;
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
import java.util.*;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenEncryptionService tokenEncryptionService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${app.google.client-id}")
    private String clientId;

    @Value("${app.google.client-secret}")
    private String clientSecret;

    @Value("${app.google.redirect-uri}")
    private String redirectUri;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationInMs;

    public GoogleOAuthServiceImpl(GoogleAccountRepository googleAccountRepository,
                                 GoogleIntegrationRepository googleIntegrationRepository,
                                 GoogleOAuthTokenRepository googleOAuthTokenRepository,
                                 GoogleSyncLogRepository googleSyncLogRepository,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 UserProfileRepository userProfileRepository,
                                 RefreshTokenRepository refreshTokenRepository,
                                 JwtTokenProvider jwtTokenProvider,
                                 TokenEncryptionService tokenEncryptionService) {
        this.googleAccountRepository = googleAccountRepository;
        this.googleIntegrationRepository = googleIntegrationRepository;
        this.googleOAuthTokenRepository = googleOAuthTokenRepository;
        this.googleSyncLogRepository = googleSyncLogRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenEncryptionService = tokenEncryptionService;
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

    @Override
    public GoogleConnectResponse getAuthorizationUrl(Long userId, String serviceName) {
        String service = serviceName != null ? serviceName.toUpperCase() : "AUTH";
        String scope = getScopeForService(service);
        String state = UUID.randomUUID().toString() + "_" + (userId != null ? userId : "guest") + "_" + service;

        String authUrl = UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("include_granted_scopes", "true")
                .queryParam("state", state)
                .build().toUriString();

        return new GoogleConnectResponse(authUrl, state, service, scope);
    }

    @Override
    @Transactional
    public void handleCallback(Long userId, String code, String state, String serviceName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String service = serviceName != null ? serviceName.toUpperCase() : "AUTH";
        try {
            String accessToken;
            String refreshToken;
            Integer expiresIn;
            String scope;
            String googleUserId;
            String googleEmail;
            String profilePic;

            if (code == null || code.isBlank()) {
                throw new BadRequestException("Authorization code is required to complete Google OAuth.");
            }

            // Exchange authorization code for Google tokens
            Map<String, Object> tokenPayload = exchangeCodeForTokens(code);
            accessToken = (String) tokenPayload.get("access_token");
            refreshToken = (String) tokenPayload.get("refresh_token");
            expiresIn = (Integer) tokenPayload.get("expires_in");
            scope = (String) tokenPayload.get("scope");

            // Fetch Google user profile
            Map<String, Object> userInfo = fetchGoogleUserInfo(accessToken);
            googleUserId = (String) userInfo.get("sub");
            googleEmail = (String) userInfo.get("email");
            profilePic = (String) userInfo.get("picture");

            Instant expiryInstant = Instant.now().plusSeconds(expiresIn != null ? expiresIn : 3600);

            // Save or update GoogleAccount
            GoogleAccount account = googleAccountRepository.findByUser(user)
                    .orElse(new GoogleAccount(null, user, googleUserId, googleEmail, profilePic, Instant.now(), Instant.now()));
            account.setGoogleUserId(googleUserId);
            account.setGoogleEmail(googleEmail);
            account.setProfilePicture(profilePic);
            account.setUpdatedAt(Instant.now());
            googleAccountRepository.save(account);

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

        // If AUTH is disconnected, disconnect all Google services
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

        googleSyncLogRepository.save(new GoogleSyncLog(
                null, user, "AUTH", "REVOKED", null, "All Google authorizations were permanently revoked.", Instant.now()
        ));
    }

    @Override
    public String getValidAccessToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) return null;

        Optional<GoogleOAuthToken> tokenOpt = googleOAuthTokenRepository.findByUser(user);
        if (tokenOpt.isEmpty()) {
            return null;
        }
        GoogleOAuthToken oauthToken = tokenOpt.get();

        // If access token is still valid (with 60-second safety buffer), return decrypted token
        if (oauthToken.getExpiresAt() != null && oauthToken.getExpiresAt().isAfter(Instant.now().plusSeconds(60))) {
            return tokenEncryptionService.decrypt(oauthToken.getEncryptedAccessToken());
        }

        // Token expired, use refresh token to acquire new access token
        if (oauthToken.getEncryptedRefreshToken() == null) {
            return null;
        }

        String refreshToken = tokenEncryptionService.decrypt(oauthToken.getEncryptedRefreshToken());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("refresh_token", refreshToken);
            body.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String newAccessToken = root.path("access_token").asText();
                int expiresIn = root.path("expires_in").asInt(3600);

                oauthToken.setEncryptedAccessToken(tokenEncryptionService.encrypt(newAccessToken));
                oauthToken.setExpiresAt(Instant.now().plusSeconds(expiresIn));
                oauthToken.setUpdatedAt(Instant.now());
                googleOAuthTokenRepository.save(oauthToken);

                return newAccessToken;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.warn("Token refresh failed for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GoogleIntegrationStatusResponse getIntegrationStatus(Long userId) {
        Optional<GoogleAccount> accountOpt = googleAccountRepository.findByUserId(userId);
        List<GoogleIntegration> integrations = googleIntegrationRepository.findByUserId(userId);
        Map<String, GoogleIntegration> map = integrations.stream()
                .collect(Collectors.toMap(i -> i.getServiceName().toUpperCase(), i -> i, (a, b) -> a));

        List<GoogleServiceStatusDto> serviceDtos = new ArrayList<>();
        String[] supportedServices = {"AUTH", "CALENDAR", "SHEETS", "DRIVE", "GMAIL", "MAPS"};
        String[] descriptions = {
                "Google OpenID Connect Sign-In & Profile Linking",
                "Two-way Financial Reminders, Subscription & Loan Calendar Sync",
                "Automated 7-Tab Financial Workbook Export & Sync",
                "Encrypted Financial Statements & Receipts Cloud Storage",
                "Direct Financial Alerts & Monthly Performance Summaries",
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
    public AuthResponse authenticateWithGoogle(GoogleAuthRequest request) {
        String googleEmail = null;
        String googleName = null;
        String googleUserId = null;
        String profilePic = null;

        try {
            if (request.getCode() != null && !request.getCode().isBlank()) {
                Map<String, Object> tokenPayload = exchangeCodeForTokens(request.getCode());
                String accessToken = (String) tokenPayload.get("access_token");
                Map<String, Object> userInfo = fetchGoogleUserInfo(accessToken);
                googleUserId = (String) userInfo.get("sub");
                googleEmail = (String) userInfo.get("email");
                googleName = (String) userInfo.get("name");
                profilePic = (String) userInfo.get("picture");
            } else if (request.getIdToken() != null && !request.getIdToken().isBlank()) {
                // If frontend used Google Identity Services / one-tap with ID token
                Map<String, Object> payload = verifyGoogleIdToken(request.getIdToken());
                googleUserId = (String) payload.get("sub");
                googleEmail = (String) payload.get("email");
                googleName = (String) payload.get("name");
                profilePic = (String) payload.get("picture");
            } else {
                throw new BadRequestException("Either authorization code or id_token is required for Google authentication.");
            }
        } catch (Exception ex) {
            log.error("Google authentication token verification failed: {}", ex.getMessage());
            throw new BadRequestException("Invalid Google credentials: " + ex.getMessage());
        }

        if (googleEmail == null || googleEmail.isBlank()) {
            throw new BadRequestException("Unable to retrieve email from Google account.");
        }

        final String finalEmail = googleEmail.toLowerCase().trim();
        final String finalName = (googleName != null && !googleName.isBlank()) ? googleName : "FINOVA Student";
        final String finalGoogleUserId = googleUserId;
        final String finalProfilePic = profilePic;

        // Check if user exists with this email
        User user = userRepository.findByEmail(finalEmail)
                .orElseGet(() -> {
                    // Create new student user with Google
                    Role studentRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                            .orElseGet(() -> roleRepository.save(new Role(null, RoleType.ROLE_STUDENT, "Student role", Instant.now())));

                    User newUser = new User();
                    newUser.setEmail(finalEmail);
                    newUser.setFullName(finalName);
                    newUser.setPassword("$2a$10$" + UUID.randomUUID().toString().replace("-", "")); // Unusable random bcrypt
                    newUser.setIsActive(true);
                    newUser.setIsVerified(true);
                    newUser.setRoles(Collections.singleton(studentRole));
                    newUser = userRepository.save(newUser);

                    UserProfile profile = new UserProfile();
                    profile.setUser(newUser);
                    profile.setCollegeName(request.getCollegeName() != null ? request.getCollegeName() : "University Campus");
                    profile.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
                    profile.setStudentStatus("ACTIVE_STUDENT");
                    profile.setCreatedAt(Instant.now());
                    profile.setUpdatedAt(Instant.now());
                    userProfileRepository.save(profile);
                    newUser.setProfile(profile);

                    return newUser;
                });

        // Ensure GoogleAccount record is linked
        GoogleAccount account = googleAccountRepository.findByUser(user)
                .orElse(new GoogleAccount(null, user, finalGoogleUserId, finalEmail, finalProfilePic, Instant.now(), Instant.now()));
        account.setGoogleUserId(finalGoogleUserId);
        account.setGoogleEmail(finalEmail);
        account.setProfilePicture(finalProfilePic);
        account.setUpdatedAt(Instant.now());
        googleAccountRepository.save(account);

        updateServiceIntegration(user, "AUTH", "openid email profile");

        // Generate FINOVA JWT & Refresh Token
        String rolesString = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.joining(","));

        String jwt = jwtTokenProvider.generateTokenFromUser(user.getId(), user.getEmail(), user.getFullName(), rolesString);

        String refreshTokenStr = UUID.randomUUID().toString();
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationInMs));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        List<String> rolesList = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        UserProfile profile = user.getProfile();

        return new AuthResponse(
                jwt,
                "Bearer",
                refreshTokenStr,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                rolesList,
                profile != null ? profile.getCollegeName() : null,
                profile != null ? profile.getCurrency() : "INR",
                profile != null ? profile.getStudentStatus() : "ACTIVE_STUDENT"
        );
    }

    private Map<String, Object> exchangeCodeForTokens(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BadRequestException("Failed to exchange Google authorization code.");
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
        return result;
    }

    private Map<String, Object> verifyGoogleIdToken(String idToken) throws Exception {
        // Query token info endpoint
        String verifyUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<String> response = restTemplate.getForEntity(verifyUrl, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BadRequestException("Google ID token is invalid or expired.");
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        Map<String, Object> result = new HashMap<>();
        result.put("sub", root.path("sub").asText());
        result.put("email", root.path("email").asText());
        result.put("name", root.path("name").asText());
        result.put("picture", root.path("picture").asText(null));
        return result;
    }
}
