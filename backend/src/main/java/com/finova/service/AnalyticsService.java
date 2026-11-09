package com.finova.service;

import com.finova.dto.dashboard.DashboardAnalyticsResponse;

import java.time.LocalDate;
import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> getFinancialSummaryReport(Long userId, String period, LocalDate startDate, LocalDate endDate);
    byte[] exportCsvTransactions(Long userId, LocalDate startDate, LocalDate endDate);
    byte[] exportExcelReport(Long userId, String period);
    byte[] exportPdfReport(Long userId, String period);
}
