package com.finova.dto.features;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChallengeParticipantResponse {


    private Long id;
    private Long challengeId;
    private String challengeTitle;
    private String challengeDescription;
    private BigDecimal targetSavings;
    private Integer durationDays;
    private String badgeName;
    private String badgeIcon;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal currentProgress;
    private Double progressPercentage;
    private String status;


    public ChallengeParticipantResponse() {}

    public ChallengeParticipantResponse(Long id, Long challengeId, String challengeTitle, String challengeDescription, BigDecimal targetSavings, Integer durationDays, String badgeName, String badgeIcon, LocalDate startDate, LocalDate endDate, BigDecimal currentProgress, Double progressPercentage, String status) {
        this.id = id;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.challengeDescription = challengeDescription;
        this.targetSavings = targetSavings;
        this.durationDays = durationDays;
        this.badgeName = badgeName;
        this.badgeIcon = badgeIcon;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentProgress = currentProgress;
        this.progressPercentage = progressPercentage;
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChallengeId() {
        return this.challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeTitle() {
        return this.challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getChallengeDescription() {
        return this.challengeDescription;
    }

    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
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

    public Double getProgressPercentage() {
        return this.progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static ChallengeParticipantResponseBuilder builder() {
        return new ChallengeParticipantResponseBuilder();
    }

    public static class ChallengeParticipantResponseBuilder {
        private Long id;
        private Long challengeId;
        private String challengeTitle;
        private String challengeDescription;
        private BigDecimal targetSavings;
        private Integer durationDays;
        private String badgeName;
        private String badgeIcon;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal currentProgress;
        private Double progressPercentage;
        private String status;

        public ChallengeParticipantResponseBuilder() {}

        public ChallengeParticipantResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ChallengeParticipantResponseBuilder challengeId(Long challengeId) {
            this.challengeId = challengeId;
            return this;
        }

        public ChallengeParticipantResponseBuilder challengeTitle(String challengeTitle) {
            this.challengeTitle = challengeTitle;
            return this;
        }

        public ChallengeParticipantResponseBuilder challengeDescription(String challengeDescription) {
            this.challengeDescription = challengeDescription;
            return this;
        }

        public ChallengeParticipantResponseBuilder targetSavings(BigDecimal targetSavings) {
            this.targetSavings = targetSavings;
            return this;
        }

        public ChallengeParticipantResponseBuilder durationDays(Integer durationDays) {
            this.durationDays = durationDays;
            return this;
        }

        public ChallengeParticipantResponseBuilder badgeName(String badgeName) {
            this.badgeName = badgeName;
            return this;
        }

        public ChallengeParticipantResponseBuilder badgeIcon(String badgeIcon) {
            this.badgeIcon = badgeIcon;
            return this;
        }

        public ChallengeParticipantResponseBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public ChallengeParticipantResponseBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public ChallengeParticipantResponseBuilder currentProgress(BigDecimal currentProgress) {
            this.currentProgress = currentProgress;
            return this;
        }

        public ChallengeParticipantResponseBuilder progressPercentage(Double progressPercentage) {
            this.progressPercentage = progressPercentage;
            return this;
        }

        public ChallengeParticipantResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ChallengeParticipantResponse build() {
            ChallengeParticipantResponse instance = new ChallengeParticipantResponse();
            instance.setId(this.id);
            instance.setChallengeId(this.challengeId);
            instance.setChallengeTitle(this.challengeTitle);
            instance.setChallengeDescription(this.challengeDescription);
            instance.setTargetSavings(this.targetSavings);
            instance.setDurationDays(this.durationDays);
            instance.setBadgeName(this.badgeName);
            instance.setBadgeIcon(this.badgeIcon);
            instance.setStartDate(this.startDate);
            instance.setEndDate(this.endDate);
            instance.setCurrentProgress(this.currentProgress);
            instance.setProgressPercentage(this.progressPercentage);
            instance.setStatus(this.status);
            return instance;
        }
    }
}
