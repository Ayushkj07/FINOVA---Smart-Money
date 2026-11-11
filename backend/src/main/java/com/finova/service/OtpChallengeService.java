package com.finova.service;

import com.finova.dto.auth.AuthResponse;
import com.finova.dto.auth.GoogleAuthResponse;
import com.finova.dto.auth.ResendOtpRequest;
import com.finova.dto.auth.VerifyOtpRequest;
import com.finova.entity.User;

public interface OtpChallengeService {
    GoogleAuthResponse createOtpChallenge(User user, String email, String fullName);
    AuthResponse verifyOtp(VerifyOtpRequest request);
    GoogleAuthResponse resendOtp(ResendOtpRequest request);
}
