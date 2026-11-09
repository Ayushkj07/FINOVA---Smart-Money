package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "challenge_participants")
public class ChallengeParticipant {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private FinancialChallenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "current_progress", precision = 12, scale = 2)
    private BigDecimal currentProgress = BigDecimal.ZERO;

    @Column(length = 30)
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED, FAILED

    @Column(name = "achieved_at")
    private Instant achievedAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public ChallengeParticipant() {}

    public ChallengeParticipant(Long id, FinancialChallenge challenge, User user, LocalDate startDate, LocalDate endDate, BigDecimal currentProgress, String status, Instant achievedAt, Instant createdAt) {
        this.id = id;
        this.challenge = challenge;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentProgress = currentProgress;
        this.status = status;
        this.achievedAt = achievedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FinancialChallenge getChallenge() {
        return this.challenge;
    }

    public void setChallenge(FinancialChallenge challenge) {
        this.challenge = challenge;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getCurrentProgress() {
        return this.currentProgress;
    }

    public void setCurrentProgress(BigDecimal currentProgress) {
        this.currentProgress = currentProgress;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getAchievedAt() {
        return this.achievedAt;
    }

    public void setAchievedAt(Instant achievedAt) {
        this.achievedAt = achievedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static ChallengeParticipantBuilder builder() {
        return new ChallengeParticipantBuilder();
    }

    public static class ChallengeParticipantBuilder {
        private Long id;
        private FinancialChallenge challenge;
        private User user;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal currentProgress;
        private String status;
        private Instant achievedAt;
        private Instant createdAt;

        public ChallengeParticipantBuilder() {}

        public ChallengeParticipantBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ChallengeParticipantBuilder challenge(FinancialChallenge challenge) {
            this.challenge = challenge;
            return this;
        }

        public ChallengeParticipantBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ChallengeParticipantBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public ChallengeParticipantBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public ChallengeParticipantBuilder currentProgress(BigDecimal currentProgress) {
            this.currentProgress = currentProgress;
            return this;
        }

        public ChallengeParticipantBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ChallengeParticipantBuilder achievedAt(Instant achievedAt) {
            this.achievedAt = achievedAt;
            return this;
        }

        public ChallengeParticipantBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ChallengeParticipant build() {
            ChallengeParticipant instance = new ChallengeParticipant();
            instance.setId(this.id);
            instance.setChallenge(this.challenge);
            instance.setUser(this.user);
            instance.setStartDate(this.startDate);
            instance.setEndDate(this.endDate);
            instance.setCurrentProgress(this.currentProgress);
            instance.setStatus(this.status);
            instance.setAchievedAt(this.achievedAt);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
