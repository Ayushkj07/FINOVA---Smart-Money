package com.finova.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Boolean revoked = false;


    public RefreshToken() {}

    public RefreshToken(Long id, User user, String token, Instant expiryDate, Instant createdAt, Boolean revoked) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
        this.revoked = revoked != null ? revoked : false;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getRevoked() {
        return this.revoked;
    }

    public Boolean isRevoked() {
        return this.revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked != null ? revoked : false;
    }

    public static RefreshTokenBuilder builder() {
        return new RefreshTokenBuilder();
    }

    public static class RefreshTokenBuilder {
        private Long id;
        private User user;
        private String token;
        private Instant expiryDate;
        private Instant createdAt;
        private Boolean revoked;

        public RefreshTokenBuilder() {}

        public RefreshTokenBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RefreshTokenBuilder user(User user) {
            this.user = user;
            return this;
        }

        public RefreshTokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        public RefreshTokenBuilder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public RefreshTokenBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RefreshTokenBuilder revoked(Boolean revoked) {
            this.revoked = revoked != null ? revoked : false;
            return this;
        }

        public RefreshToken build() {
            RefreshToken instance = new RefreshToken();
            instance.setId(this.id);
            instance.setUser(this.user);
            instance.setToken(this.token);
            instance.setExpiryDate(this.expiryDate);
            instance.setCreatedAt(this.createdAt != null ? this.createdAt : java.time.Instant.now());
            instance.setRevoked(this.revoked != null ? this.revoked : false);
            return instance;
        }
    }
}
