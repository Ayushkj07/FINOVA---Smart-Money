package com.finova.service.impl;

import com.finova.dto.auth.*;
import com.finova.entity.*;
import com.finova.entity.enums.RoleType;
import com.finova.exception.BadRequestException;
import com.finova.exception.ResourceNotFoundException;
import com.finova.repository.*;
import com.finova.security.JwtTokenProvider;
import com.finova.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationInMs;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create or rotate refresh token
        String refreshTokenStr = UUID.randomUUID().toString();
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiryDate(Instant.now().plusMillis(refreshExpirationInMs))
                .build();
        refreshTokenRepository.save(refreshToken);

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        UserProfile profile = user.getProfile();

        return AuthResponse.builder()
                .token(jwt)
                .refreshToken(refreshTokenStr)
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles)
                .collegeName(profile != null ? profile.getCollegeName() : null)
                .currency(profile != null ? profile.getCurrency() : "INR")
                .studentStatus(profile != null ? profile.getStudentStatus() : "ACTIVE_STUDENT")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role studentRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_STUDENT)
                        .description("Student role")
                        .build()));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .isVerified(true) // Auto-verified for seamless student onboarding
                .roles(Collections.singleton(studentRole))
                .build();

        user = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .collegeName(request.getCollegeName())
                .studentStatus(request.getStudentStatus() != null ? request.getStudentStatus() : "ACTIVE_STUDENT")
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .build();
        userProfileRepository.save(profile);
        user.setProfile(profile);

        // Auto login on register
        String rolesStr = RoleType.ROLE_STUDENT.name();
        String jwt = tokenProvider.generateTokenFromUser(user.getId(), user.getEmail(), user.getFullName(), rolesStr);

        String refreshTokenStr = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiryDate(Instant.now().plusMillis(refreshExpirationInMs))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .token(jwt)
                .refreshToken(refreshTokenStr)
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(Collections.singletonList(rolesStr))
                .collegeName(profile.getCollegeName())
                .currency(profile.getCurrency())
                .studentStatus(profile.getStudentStatus())
                .build();
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

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
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
            log.info("Password reset token generated for user {}: {}", user.getEmail(), token);
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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, UserProfileRepository userProfileRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

}
