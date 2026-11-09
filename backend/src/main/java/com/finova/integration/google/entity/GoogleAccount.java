package com.finova.integration.google.entity;

import com.finova.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "google_accounts", uniqueConstraints = {
    @UniqueConstraint(name = "uk_google_accounts_user", columnNames = {"user_id"}),
    @UniqueConstraint(name = "uk_google_accounts_google_id", columnNames = {"google_user_id"})
})
public class GoogleAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "google_user_id", nullable = false, length = 100)
    private String googleUserId;

    @Column(name = "google_email", nullable = false, length = 150)
    private String googleEmail;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public GoogleAccount() {}

    public GoogleAccount(Long id, User user, String googleUserId, String googleEmail, String profilePicture, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.googleUserId = googleUserId;
        this.googleEmail = googleEmail;
        this.profilePicture = profilePicture;
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

    public String getGoogleUserId() { return googleUserId; }
    public void setGoogleUserId(String googleUserId) { this.googleUserId = googleUserId; }

    public String getGoogleEmail() { return googleEmail; }
    public void setGoogleEmail(String googleEmail) { this.googleEmail = googleEmail; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static GoogleAccountBuilder builder() {
        return new GoogleAccountBuilder();
    }

    public static class GoogleAccountBuilder {
        private Long id;
        private User user;
        private String googleUserId;
        private String googleEmail;
        private String profilePicture;
        private Instant createdAt;
        private Instant updatedAt;

        public GoogleAccountBuilder id(Long id) { this.id = id; return this; }
        public GoogleAccountBuilder user(User user) { this.user = user; return this; }
        public GoogleAccountBuilder googleUserId(String googleUserId) { this.googleUserId = googleUserId; return this; }
        public GoogleAccountBuilder googleEmail(String googleEmail) { this.googleEmail = googleEmail; return this; }
        public GoogleAccountBuilder profilePicture(String profilePicture) { this.profilePicture = profilePicture; return this; }
        public GoogleAccountBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public GoogleAccountBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public GoogleAccount build() {
            return new GoogleAccount(id, user, googleUserId, googleEmail, profilePicture, createdAt, updatedAt);
        }
    }
}
