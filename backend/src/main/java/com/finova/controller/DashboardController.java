package com.finova.controller;

import com.finova.dto.dashboard.DashboardAnalyticsResponse;
import com.finova.dto.dashboard.DashboardSummaryResponse;
import com.finova.dto.dashboard.HealthScoreResponse;
import com.finova.security.UserPrincipal;
import com.finova.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Student financial overview metrics, charts, and health score")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get dynamically calculated student financial summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "THIS_MONTH") String timeFilter) {
        return ResponseEntity.ok(dashboardService.getSummary(userPrincipal.getId(), timeFilter));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get visual analytics and chart data points")
    public ResponseEntity<DashboardAnalyticsResponse> getAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "THIS_MONTH") String timeFilter) {
        return ResponseEntity.ok(dashboardService.getAnalytics(userPrincipal.getId(), timeFilter));
    }

    @GetMapping("/health-score")
    @Operation(summary = "Get educational financial health score (0-100) and actionable tips")
    public ResponseEntity<HealthScoreResponse> getHealthScore(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(dashboardService.getHealthScore(userPrincipal.getId()));
    }

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

}
