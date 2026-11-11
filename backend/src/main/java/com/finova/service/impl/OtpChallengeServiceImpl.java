package com.finova.service.impl;

import com.finova.dto.auth.AuthResponse;
import com.finova.dto.auth.GoogleAuthResponse;
import com.finova.dto.auth.ResendOtpRequest;
import com.finova.dto.auth.VerifyOtpRequest;
import com.finova.entity.LoginOtpChallenge;
import com.finova.entity.RefreshToken;
import com.finova.entity.User;
import com.finova.entity.UserProfile;
import com.finova.exception.BadRequestException;
import com.finova.repository.LoginOtpChallengeRepository;
import com.finova.repository.RefreshTokenRepository;
import com.finova.repository.UserRepository;
import com.finova.security.JwtTokenProvider;
import com.finova.service.EmailService;
import com.finova.service.OtpChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OtpChallengeServiceImpl implements OtpChallengeService {

    private static final Logger log = LoggerFactory.getLogger(OtpChallengeServiceImpl.class);

    private final LoginOtpChallengeRepository loginOtpChallengeRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.expiration-minutes:5}")
    private int expirationMinutes;

    @Value("${app.otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.otp.resend-cooldown-seconds:60}")
    private int resendCooldownSeconds;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationInMs;

    public OtpChallengeServiceImpl(LoginOtpChallengeRepository loginOtpChallengeRepository,
                                  UserRepository userRepository,
                                  RefreshTokenRepository refreshTokenRepository,
                                  EmailService emailService,
                                  PasswordEncoder passwordEncoder,
                                  JwtTokenProvider jwtTokenProvider) {
        this.loginOtpChallengeRepository = loginOtpChallengeRepository;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public GoogleAuthResponse createOtpChallenge(User user, String email, String fullName) {
        final String cleanEmail = email.toLowerCase().trim();

        // Invalidate any previous unused challenges for this email
        List<LoginOtpChallenge> previous = loginOtpChallengeRepository.findByEmailAndUsedAtIsNull(cleanEmail);
        for (LoginOtpChallenge p : previous) {
            p.setUsedAt(Instant.now());
        }
        if (!previous.isEmpty()) {
            loginOtpChallengeRepository.saveAll(previous);
        }

        // Generate cryptographically secure 6-digit numeric OTP
        int rawCode = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(rawCode);

        // Hash OTP before storing (never store in plain text!)
        String otpHash = passwordEncoder.encode(otp);

        String challengeId = "chal_" + UUID.randomUUID().toString().replace("-", "");
        Instant expiresAt = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);

        LoginOtpChallenge challenge = LoginOtpChallenge.builder()
                .challengeId(challengeId)
                .user(user)
                .email(cleanEmail)
                .otpHash(otpHash)
                .expiresAt(expiresAt)
                .attempts(0)
                .createdAt(Instant.now())
                .build();

        loginOtpChallengeRepository.save(challenge);

        // Deliver OTP via real email service
        emailService.sendLoginOtpEmail(cleanEmail, fullName, otp, expirationMinutes);
        log.info("Issued secure OTP challenge for user {} (expires in {} minutes)", cleanEmail, expirationMinutes);

        return new GoogleAuthResponse(
                true,
                challengeId,
                cleanEmail,
                maskEmail(cleanEmail),
                expirationMinutes * 60L,
                (long) resendCooldownSeconds,
                "A 6-digit verification code has been sent to " + maskEmail(cleanEmail)
        );
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        LoginOtpChallenge challenge = loginOtpChallengeRepository.findByChallengeId(request.getChallengeId())
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification session. Please sign in again."));

        if (challenge.getUsedAt() != null) {
            throw new BadRequestException("This verification code has already been used. Please sign in again.");
        }

        if (challenge.getExpiresAt().isBefore(Instant.now())) {
            challenge.setUsedAt(Instant.now());
            loginOtpChallengeRepository.save(challenge);
            throw new BadRequestException("The verification code has expired. Please sign in again to request a new code.");
        }

        if (challenge.getAttempts() >= maxAttempts) {
            challenge.setUsedAt(Instant.now());
            loginOtpChallengeRepository.save(challenge);
            throw new BadRequestException("Too many incorrect attempts. This verification session has been terminated for security.");
        }

        challenge.setAttempts(challenge.getAttempts() + 1);

        if (!passwordEncoder.matches(request.getOtp(), challenge.getOtpHash())) {
            loginOtpChallengeRepository.save(challenge);
            int remaining = maxAttempts - challenge.getAttempts();
            if (remaining <= 0) {
                challenge.setUsedAt(Instant.now());
                loginOtpChallengeRepository.save(challenge);
                throw new BadRequestException("Too many incorrect attempts. This verification session has been terminated.");
            }
            throw new BadRequestException("Invalid verification code. Attempts remaining: " + remaining);
        }

        // OTP is valid! Mark challenge verified and used
        challenge.setVerifiedAt(Instant.now());
        challenge.setUsedAt(Instant.now());
        loginOtpChallengeRepository.save(challenge);

        User user = challenge.getUser();
        user.setLastLogin(Instant.now());
        user.setIsVerified(true);
        userRepository.save(user);

        // Issue real FINOVA Access and Refresh Tokens
        String rolesString = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.joining(","));

        String jwt = jwtTokenProvider.generateTokenFromUser(user.getId(), user.getEmail(), user.getFullName(), rolesString);

        String refreshTokenStr = UUID.randomUUID().toString();
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiryDate(Instant.now().plusMillis(refreshExpirationInMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        List<String> rolesList = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        UserProfile profile = user.getProfile();

        log.info("OTP verification succeeded for user ID {} ({})", user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .refreshToken(refreshTokenStr)
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(rolesList)
                .collegeName(profile != null ? profile.getCollegeName() : null)
                .currency(profile != null ? profile.getCurrency() : "INR")
                .studentStatus(profile != null ? profile.getStudentStatus() : "ACTIVE_STUDENT")
                .build();
    }

    @Override
    @Transactional
    public GoogleAuthResponse resendOtp(ResendOtpRequest request) {
        LoginOtpChallenge challenge = loginOtpChallengeRepository.findByChallengeId(request.getChallengeId())
                .orElseThrow(() -> new BadRequestException("Invalid verification session. Please sign in again."));

        if (challenge.getUsedAt() != null) {
            throw new BadRequestException("This verification session is no longer active. Please sign in again.");
        }

        // Check 60-second cooldown
        Instant cooldownEnd = challenge.getCreatedAt().plus(resendCooldownSeconds, ChronoUnit.SECONDS);
        if (Instant.now().isBefore(cooldownEnd)) {
            long remainingSeconds = Duration.between(Instant.now(), cooldownEnd).getSeconds();
            throw new BadRequestException("Please wait " + Math.max(1, remainingSeconds) + " seconds before requesting a new code.");
        }

        // Invalidate previous challenge
        challenge.setUsedAt(Instant.now());
        loginOtpChallengeRepository.save(challenge);

        // Issue new challenge
        User user = challenge.getUser();
        return createOtpChallenge(user, challenge.getEmail(), user.getFullName());
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "your email";
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) {
            return name.charAt(0) + "***@" + domain;
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + "@" + domain;
    }
}
