package com.finova.service;

import com.finova.dto.dashboard.DashboardAnalyticsResponse;
import com.finova.dto.dashboard.DashboardSummaryResponse;
import com.finova.dto.dashboard.HealthScoreResponse;

public interface DashboardService {
    DashboardSummaryResponse getSummary(Long userId, String timeFilter);
    DashboardAnalyticsResponse getAnalytics(Long userId, String timeFilter);
    HealthScoreResponse getHealthScore(Long userId);
}
