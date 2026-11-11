package com.finova.controller;

import com.finova.dto.auth.*;
import com.finova.integration.google.dto.GoogleAuthRequest;
import com.finova.integration.google.service.GoogleOAuthService;
import com.finova.security.UserPrincipal;
import com.finova.service.AuthService;
import com.finova.service.OtpChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Student & Admin authentication, registration, Google OAuth with Email OTP verification")
public class AuthController {

    private final AuthService authService;
    private final GoogleOAuthService googleOAuthService;
    private final OtpChallengeService otpChallengeService;

    public AuthController(AuthService authService,
                          GoogleOAuthService googleOAuthService,
                          OtpChallengeService otpChallengeService) {
        this.authService = authService;
        this.googleOAuthService = googleOAuthService;
        this.otpChallengeService = otpChallengeService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new student account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/google")
    @Operation(summary = "Authenticate with Google and generate 6-digit Email OTP challenge")
    public ResponseEntity<GoogleAuthResponse> googleAuth(@RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(googleOAuthService.authenticateWithGoogle(request));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify 6-digit email OTP and issue authenticated FINOVA session")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(otpChallengeService.verifyOtp(request));
    }

    @PostMapping("/otp/resend")
    @Operation(summary = "Resend 6-digit email OTP verification code with 60s cooldown")
    public ResponseEntity<GoogleAuthResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(otpChallengeService.resendOtp(request));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify student email address using token")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully. You can now access your FINOVA account."));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT access token using refresh token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and revoke refresh token")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null) {
            authService.logout(userPrincipal.getId());
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated student profile")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(authService.getProfile(userPrincipal.getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current student profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(userPrincipal.getId(), request));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset email/token")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
