package com.finova.integration.google.entity;

import com.finova.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "google_oauth_tokens", uniqueConstraints = {
    @UniqueConstraint(name = "uk_google_tokens_user", columnNames = {"user_id"})
})
public class GoogleOAuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "encrypted_access_token", length = 2000)
    private String encryptedAccessToken;

    @Column(name = "encrypted_refresh_token", length = 2000)
    private String encryptedRefreshToken;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public GoogleOAuthToken() {}

    public GoogleOAuthToken(Long id, User user, String encryptedAccessToken, String encryptedRefreshToken, Instant expiresAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.encryptedAccessToken = encryptedAccessToken;
        this.encryptedRefreshToken = encryptedRefreshToken;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEncryptedAccessToken() { return encryptedAccessToken; }
    public void setEncryptedAccessToken(String encryptedAccessToken) { this.encryptedAccessToken = encryptedAccessToken; }

    public String getEncryptedRefreshToken() { return encryptedRefreshToken; }
    public void setEncryptedRefreshToken(String encryptedRefreshToken) { this.encryptedRefreshToken = encryptedRefreshToken; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static GoogleOAuthTokenBuilder builder() {
        return new GoogleOAuthTokenBuilder();
    }

    public static class GoogleOAuthTokenBuilder {
        private Long id;
        private User user;
        private String encryptedAccessToken;
        private String encryptedRefreshToken;
        private Instant expiresAt;
        private Instant createdAt;
        private Instant updatedAt;

        public GoogleOAuthTokenBuilder id(Long id) { this.id = id; return this; }
        public GoogleOAuthTokenBuilder user(User user) { this.user = user; return this; }
        public GoogleOAuthTokenBuilder encryptedAccessToken(String encryptedAccessToken) { this.encryptedAccessToken = encryptedAccessToken; return this; }
        public GoogleOAuthTokenBuilder encryptedRefreshToken(String encryptedRefreshToken) { this.encryptedRefreshToken = encryptedRefreshToken; return this; }
        public GoogleOAuthTokenBuilder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public GoogleOAuthTokenBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public GoogleOAuthTokenBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public GoogleOAuthToken build() {
            return new GoogleOAuthToken(id, user, encryptedAccessToken, encryptedRefreshToken, expiresAt, createdAt, updatedAt);
        }
    }
}
