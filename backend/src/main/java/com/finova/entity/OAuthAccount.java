package com.finova.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "oauth_accounts", uniqueConstraints = {
    @UniqueConstraint(name = "uk_oauth_provider_user", columnNames = {"provider", "provider_user_id"})
})
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider; // e.g. "GOOGLE"

    @Column(name = "provider_user_id", nullable = false, length = 150)
    private String providerUserId; // Google sub identifier

    @Column(name = "provider_email", length = 150)
    private String providerEmail;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public OAuthAccount() {}

    public OAuthAccount(Long id, User user, String provider, String providerUserId, String providerEmail, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.providerEmail = providerEmail;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static OAuthAccountBuilder builder() {
        return new OAuthAccountBuilder();
    }

    public static class OAuthAccountBuilder {
        private Long id;
        private User user;
        private String provider;
        private String providerUserId;
        private String providerEmail;
        private Instant createdAt;

        public OAuthAccountBuilder() {}

        public OAuthAccountBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public OAuthAccountBuilder user(User user) {
            this.user = user;
            return this;
        }

        public OAuthAccountBuilder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public OAuthAccountBuilder providerUserId(String providerUserId) {
            this.providerUserId = providerUserId;
            return this;
        }

        public OAuthAccountBuilder providerEmail(String providerEmail) {
            this.providerEmail = providerEmail;
            return this;
        }

        public OAuthAccountBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OAuthAccount build() {
            return new OAuthAccount(id, user, provider, providerUserId, providerEmail, createdAt);
        }
    }
}
