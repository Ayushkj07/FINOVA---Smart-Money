package com.finova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "financial_challenges")
public class FinancialChallenge {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(name = "target_savings", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetSavings;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "badge_name", length = 100)
    private String badgeName;

    @Column(name = "badge_icon", length = 50)
    private String badgeIcon;

    @Column(length = 50)
    private String category = "GENERAL";

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public FinancialChallenge() {}

    public FinancialChallenge(Long id, String title, String description, BigDecimal targetSavings, Integer durationDays, String badgeName, String badgeIcon, String category, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetSavings = targetSavings;
        this.durationDays = durationDays;
        this.badgeName = badgeName;
        this.badgeIcon = badgeIcon;
        this.category = category;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTargetSavings() {
        return this.targetSavings;
    }

    public void setTargetSavings(BigDecimal targetSavings) {
        this.targetSavings = targetSavings;
    }

    public Integer getDurationDays() {
        return this.durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getBadgeName() {
        return this.badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getBadgeIcon() {
        return this.badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static FinancialChallengeBuilder builder() {
        return new FinancialChallengeBuilder();
    }

    public static class FinancialChallengeBuilder {
        private Long id;
        private String title;
        private String description;
        private BigDecimal targetSavings;
        private Integer durationDays;
        private String badgeName;
        private String badgeIcon;
        private String category;
        private Instant createdAt;

        public FinancialChallengeBuilder() {}

        public FinancialChallengeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FinancialChallengeBuilder title(String title) {
            this.title = title;
            return this;
        }

        public FinancialChallengeBuilder description(String description) {
            this.description = description;
            return this;
        }

        public FinancialChallengeBuilder targetSavings(BigDecimal targetSavings) {
            this.targetSavings = targetSavings;
            return this;
        }

        public FinancialChallengeBuilder durationDays(Integer durationDays) {
            this.durationDays = durationDays;
            return this;
        }

        public FinancialChallengeBuilder badgeName(String badgeName) {
            this.badgeName = badgeName;
            return this;
        }

        public FinancialChallengeBuilder badgeIcon(String badgeIcon) {
            this.badgeIcon = badgeIcon;
            return this;
        }

        public FinancialChallengeBuilder category(String category) {
            this.category = category;
            return this;
        }

        public FinancialChallengeBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FinancialChallenge build() {
            FinancialChallenge instance = new FinancialChallenge();
            instance.setId(this.id);
            instance.setTitle(this.title);
            instance.setDescription(this.description);
            instance.setTargetSavings(this.targetSavings);
            instance.setDurationDays(this.durationDays);
            instance.setBadgeName(this.badgeName);
            instance.setBadgeIcon(this.badgeIcon);
            instance.setCategory(this.category);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
