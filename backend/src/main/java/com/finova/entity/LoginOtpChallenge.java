package com.finova.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "login_otp_challenges", indexes = {
    @Index(name = "idx_otp_challenge_id", columnList = "challenge_id", unique = true),
    @Index(name = "idx_otp_user_email", columnList = "email")
})
public class LoginOtpChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "challenge_id", nullable = false, unique = true, length = 100)
    private String challengeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public LoginOtpChallenge() {}

    public LoginOtpChallenge(Long id, String challengeId, User user, String email, String otpHash, Instant expiresAt, Integer attempts, Instant verifiedAt, Instant usedAt, Instant createdAt) {
        this.id = id;
        this.challengeId = challengeId;
        this.user = user;
        this.email = email;
        this.otpHash = otpHash;
        this.expiresAt = expiresAt;
        this.attempts = attempts != null ? attempts : 0;
        this.verifiedAt = verifiedAt;
        this.usedAt = usedAt;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtpHash() { return otpHash; }
    public void setOtpHash(String otpHash) { this.otpHash = otpHash; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }

    public Instant getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; }

    public Instant getUsedAt() { return usedAt; }
    public void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static LoginOtpChallengeBuilder builder() {
        return new LoginOtpChallengeBuilder();
    }

    public static class LoginOtpChallengeBuilder {
        private Long id;
        private String challengeId;
        private User user;
        private String email;
        private String otpHash;
        private Instant expiresAt;
        private Integer attempts = 0;
        private Instant verifiedAt;
        private Instant usedAt;
        private Instant createdAt;

        public LoginOtpChallengeBuilder id(Long id) { this.id = id; return this; }
        public LoginOtpChallengeBuilder challengeId(String challengeId) { this.challengeId = challengeId; return this; }
        public LoginOtpChallengeBuilder user(User user) { this.user = user; return this; }
        public LoginOtpChallengeBuilder email(String email) { this.email = email; return this; }
        public LoginOtpChallengeBuilder otpHash(String otpHash) { this.otpHash = otpHash; return this; }
        public LoginOtpChallengeBuilder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public LoginOtpChallengeBuilder attempts(Integer attempts) { this.attempts = attempts; return this; }
        public LoginOtpChallengeBuilder verifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; return this; }
        public LoginOtpChallengeBuilder usedAt(Instant usedAt) { this.usedAt = usedAt; return this; }
        public LoginOtpChallengeBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public LoginOtpChallenge build() {
            return new LoginOtpChallenge(id, challengeId, user, email, otpHash, expiresAt, attempts, verifiedAt, usedAt, createdAt);
        }
    }
}
