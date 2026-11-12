package com.finova.service.impl;

import com.finova.dto.auth.*;
import com.finova.entity.*;
import com.finova.entity.enums.RoleType;
import com.finova.exception.BadRequestException;
import com.finova.exception.ResourceNotFoundException;
import com.finova.repository.*;
import com.finova.security.JwtTokenProvider;
import com.finova.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import com.finova.dto.auth.GoogleAuthResponse;
import com.finova.service.EmailService;
import com.finova.service.OtpChallengeService;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final OtpChallengeService otpChallengeService;
    private final EmailService emailService;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationInMs;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserProfileRepository userProfileRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           OtpChallengeService otpChallengeService,
                           EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.otpChallengeService = otpChallengeService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password. Please verify your credentials."));

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password. Please verify your credentials.");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadRequestException("Your account has been deactivated. Please contact support.");
        }

        // If student account is unverified, require email OTP verification before login
        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            GoogleAuthResponse challenge = otpChallengeService.createOtpChallenge(user, user.getEmail(), user.getFullName());
            return AuthResponse.builder()
                    .otpRequired(true)
                    .challengeId(challenge.getChallengeId())
                    .maskedEmail(challenge.getMaskedEmail())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .expiresInSeconds(challenge.getExpiresInSeconds())
                    .resendCooldownSeconds(challenge.getResendCooldownSeconds())
                    .build();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);

        // Update last login timestamp
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        // Create or rotate refresh token
        String refreshTokenStr = UUID.randomUUID().toString();
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiryDate(Instant.now().plusMillis(refreshExpirationInMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        UserProfile profile = user.getProfile();

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .refreshToken(refreshTokenStr)
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles)
                .collegeName(profile != null ? profile.getCollegeName() : null)
                .currency(profile != null ? profile.getCurrency() : "INR")
                .studentStatus(profile != null ? profile.getStudentStatus() : "ACTIVE_STUDENT")
                .otpRequired(false)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match. Please re-enter your password.");
        }

        if (request.getPassword().length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long.");
        }

        String email = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email address already exists. Please sign in instead.");
        }

        Role studentRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_STUDENT)
                        .description("Student role")
                        .build()));

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .isVerified(false) // Pending OTP verification
                .accountStatus("PENDING_VERIFICATION")
                .verificationToken(verificationToken)
                .roles(new HashSet<>(Collections.singleton(studentRole)))
                .build();

        user = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .collegeName(request.getCollegeName() != null ? request.getCollegeName().trim() : "Campus University")
                .studentStatus(request.getStudentStatus() != null ? request.getStudentStatus() : "ACTIVE_STUDENT")
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .timezone("Asia/Kolkata")
                .notificationEmailEnabled(true)
                .notificationInAppEnabled(true)
                .build();
        userProfileRepository.save(profile);
        user.setProfile(profile);

        // Generate and dispatch 6-digit Email OTP Verification challenge
        GoogleAuthResponse challenge = otpChallengeService.createOtpChallenge(user, user.getEmail(), user.getFullName());

        log.info("New student registered (pending OTP verification): id={}, email={}", user.getId(), user.getEmail());

        return AuthResponse.builder()
                .otpRequired(true)
                .challengeId(challenge.getChallengeId())
                .maskedEmail(challenge.getMaskedEmail())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .expiresInSeconds(challenge.getExpiresInSeconds())
                .resendCooldownSeconds(challenge.getResendCooldownSeconds())
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Verification token is required");
        }
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification token"));

        user.setIsVerified(true);
        user.setVerificationToken(null);
        user.setAccountStatus("ACTIVE");
        userRepository.save(user);
        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.getRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token is expired or revoked. Please log in again.");
        }

        User user = refreshToken.getUser();
        String rolesStr = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.joining(","));

        String newJwt = tokenProvider.generateTokenFromUser(user.getId(), user.getEmail(), user.getFullName(), rolesStr);

        // Rotate refresh token
        String newRefreshTokenStr = UUID.randomUUID().toString();
        refreshToken.setToken(newRefreshTokenStr);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationInMs));
        refreshTokenRepository.save(refreshToken);

        UserProfile profile = user.getProfile();

        return AuthResponse.builder()
                .token(newJwt)
                .tokenType("Bearer")
                .refreshToken(newRefreshTokenStr)
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .collegeName(profile != null ? profile.getCollegeName() : null)
                .currency(profile != null ? profile.getCurrency() : "INR")
                .studentStatus(profile != null ? profile.getStudentStatus() : "ACTIVE_STUDENT")
                .build();
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserProfile profile = user.getProfile();

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .collegeName(profile != null ? profile.getCollegeName() : null)
                .studentStatus(profile != null ? profile.getStudentStatus() : null)
                .currency(profile != null ? profile.getCurrency() : "INR")
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .phoneNumber(profile != null ? profile.getPhoneNumber() : null)
                .monthlyAllowance(profile != null ? profile.getMonthlyAllowance() : null)
                .financialGoalSummary(profile != null ? profile.getFinancialGoalSummary() : null)
                .timezone(profile != null ? profile.getTimezone() : "Asia/Kolkata")
                .notificationEmailEnabled(profile != null && profile.getNotificationEmailEnabled())
                .notificationInAppEnabled(profile != null && profile.getNotificationInAppEnabled())
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        if (request.getCollegeName() != null) profile.setCollegeName(request.getCollegeName());
        if (request.getStudentStatus() != null) profile.setStudentStatus(request.getStudentStatus());
        if (request.getCurrency() != null) profile.setCurrency(request.getCurrency());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getPhoneNumber() != null) profile.setPhoneNumber(request.getPhoneNumber());
        if (request.getMonthlyAllowance() != null) profile.setMonthlyAllowance(request.getMonthlyAllowance());
        if (request.getFinancialGoalSummary() != null) profile.setFinancialGoalSummary(request.getFinancialGoalSummary());
        if (request.getTimezone() != null) profile.setTimezone(request.getTimezone());
        if (request.getNotificationEmailEnabled() != null) profile.setNotificationEmailEnabled(request.getNotificationEmailEnabled());
        if (request.getNotificationInAppEnabled() != null) profile.setNotificationInAppEnabled(request.getNotificationInAppEnabled());

        userProfileRepository.save(profile);
        userRepository.save(user);

        return getProfile(userId);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getPassword() != null && !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().toLowerCase().trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetPasswordExpiry(Instant.now().plusSeconds(3600)); // 1 hour
            userRepository.save(user);
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
            log.info("Dispatched password reset token for registered account");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        if (user.getResetPasswordExpiry() == null || user.getResetPasswordExpiry().isBefore(Instant.now())) {
            throw new BadRequestException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInitialAdminConfigured() {
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN).orElse(null);
        if (adminRole == null) return false;
        return userRepository.existsByRolesContaining(adminRole);
    }

    @Override
    @Transactional
    public void setupInitialAdmin(InitialAdminSetupRequest request) {
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_ADMIN)
                        .description("Platform administrator")
                        .build()));

        boolean adminExists = userRepository.existsByRolesContaining(adminRole);
        if (adminExists) {
            boolean activeAdminExists = userRepository.findByRolesContaining(adminRole).stream()
                    .anyMatch(u -> Boolean.TRUE.equals(u.getIsActive()));
            if (activeAdminExists) {
                throw new BadRequestException("An administrator account is already established. Initial admin setup is permanently locked.");
            }
        }

        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new BadRequestException("An account with this email already exists.");
        }

        User admin = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .fullName(request.getFullName().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .isVerified(true)
                .accountStatus("ACTIVE")
                .roles(new HashSet<>(Collections.singleton(adminRole)))
                .build();
        admin.setEmailVerified(true);
        admin = userRepository.save(admin);

        UserProfile adminProfile = UserProfile.builder()
                .user(admin)
                .collegeName("Platform Administration")
                .studentStatus("Administrator")
                .currency("INR")
                .timezone("Asia/Kolkata")
                .build();
        userProfileRepository.save(adminProfile);

        log.info("Administrator account successfully created via secure initial-setup endpoint: {}", admin.getEmail());
    }
}
