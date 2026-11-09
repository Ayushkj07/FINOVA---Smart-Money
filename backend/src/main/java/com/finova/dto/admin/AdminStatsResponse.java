package com.finova.dto.admin;

import java.math.BigDecimal;

public class AdminStatsResponse {


    private long totalUsers;
    private long activeUsers;
    private long newRegistrationsThisMonth;
    private long totalTransactionsRecorded;
    private BigDecimal totalPlatformVolume;
    private long totalAiQueries;
    private String systemStatus;
    private String databaseHealth;
    private long openSupportTickets;


    public AdminStatsResponse() {}

    public AdminStatsResponse(long totalUsers, long activeUsers, long newRegistrationsThisMonth, long totalTransactionsRecorded, BigDecimal totalPlatformVolume, long totalAiQueries, String systemStatus, String databaseHealth, long openSupportTickets) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.newRegistrationsThisMonth = newRegistrationsThisMonth;
        this.totalTransactionsRecorded = totalTransactionsRecorded;
        this.totalPlatformVolume = totalPlatformVolume;
        this.totalAiQueries = totalAiQueries;
        this.systemStatus = systemStatus;
        this.databaseHealth = databaseHealth;
        this.openSupportTickets = openSupportTickets;
    }

    public long getTotalUsers() {
        return this.totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return this.activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getNewRegistrationsThisMonth() {
        return this.newRegistrationsThisMonth;
    }

    public void setNewRegistrationsThisMonth(long newRegistrationsThisMonth) {
        this.newRegistrationsThisMonth = newRegistrationsThisMonth;
    }

    public long getTotalTransactionsRecorded() {
        return this.totalTransactionsRecorded;
    }

    public void setTotalTransactionsRecorded(long totalTransactionsRecorded) {
        this.totalTransactionsRecorded = totalTransactionsRecorded;
    }

    public BigDecimal getTotalPlatformVolume() {
        return this.totalPlatformVolume;
    }

    public void setTotalPlatformVolume(BigDecimal totalPlatformVolume) {
        this.totalPlatformVolume = totalPlatformVolume;
    }

    public long getTotalAiQueries() {
        return this.totalAiQueries;
    }

    public void setTotalAiQueries(long totalAiQueries) {
        this.totalAiQueries = totalAiQueries;
    }

    public String getSystemStatus() {
        return this.systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }

    public String getDatabaseHealth() {
        return this.databaseHealth;
    }

    public void setDatabaseHealth(String databaseHealth) {
        this.databaseHealth = databaseHealth;
    }

    public long getOpenSupportTickets() {
        return this.openSupportTickets;
    }

    public void setOpenSupportTickets(long openSupportTickets) {
        this.openSupportTickets = openSupportTickets;
    }

    public static AdminStatsResponseBuilder builder() {
        return new AdminStatsResponseBuilder();
    }

    public static class AdminStatsResponseBuilder {
        private long totalUsers;
        private long activeUsers;
        private long newRegistrationsThisMonth;
        private long totalTransactionsRecorded;
        private BigDecimal totalPlatformVolume;
        private long totalAiQueries;
        private String systemStatus;
        private String databaseHealth;
        private long openSupportTickets;

        public AdminStatsResponseBuilder() {}

        public AdminStatsResponseBuilder totalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }

        public AdminStatsResponseBuilder activeUsers(long activeUsers) {
            this.activeUsers = activeUsers;
            return this;
        }

        public AdminStatsResponseBuilder newRegistrationsThisMonth(long newRegistrationsThisMonth) {
            this.newRegistrationsThisMonth = newRegistrationsThisMonth;
            return this;
        }

        public AdminStatsResponseBuilder totalTransactionsRecorded(long totalTransactionsRecorded) {
            this.totalTransactionsRecorded = totalTransactionsRecorded;
            return this;
        }

        public AdminStatsResponseBuilder totalPlatformVolume(BigDecimal totalPlatformVolume) {
            this.totalPlatformVolume = totalPlatformVolume;
            return this;
        }

        public AdminStatsResponseBuilder totalAiQueries(long totalAiQueries) {
            this.totalAiQueries = totalAiQueries;
            return this;
        }

        public AdminStatsResponseBuilder systemStatus(String systemStatus) {
            this.systemStatus = systemStatus;
            return this;
        }

        public AdminStatsResponseBuilder databaseHealth(String databaseHealth) {
            this.databaseHealth = databaseHealth;
            return this;
        }

        public AdminStatsResponseBuilder openSupportTickets(long openSupportTickets) {
            this.openSupportTickets = openSupportTickets;
            return this;
        }

        public AdminStatsResponse build() {
            AdminStatsResponse instance = new AdminStatsResponse();
            instance.setTotalUsers(this.totalUsers);
            instance.setActiveUsers(this.activeUsers);
            instance.setNewRegistrationsThisMonth(this.newRegistrationsThisMonth);
            instance.setTotalTransactionsRecorded(this.totalTransactionsRecorded);
            instance.setTotalPlatformVolume(this.totalPlatformVolume);
            instance.setTotalAiQueries(this.totalAiQueries);
            instance.setSystemStatus(this.systemStatus);
            instance.setDatabaseHealth(this.databaseHealth);
            instance.setOpenSupportTickets(this.openSupportTickets);
            return instance;
        }
    }
}
