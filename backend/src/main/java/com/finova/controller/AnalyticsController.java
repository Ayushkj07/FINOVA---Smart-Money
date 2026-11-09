package com.finova.controller;

import com.finova.security.UserPrincipal;
import com.finova.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Financial Analytics & Reports", description = "Summary reports and document exports (PDF, Excel, CSV)")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @Operation(summary = "Get aggregated financial statement summary")
    public ResponseEntity<Map<String, Object>> getSummaryReport(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "MONTHLY") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getFinancialSummaryReport(userPrincipal.getId(), period, startDate, endDate));
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export student transactions as CSV")
    public ResponseEntity<byte[]> exportCsv(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] csvData = analyticsService.exportCsvTransactions(userPrincipal.getId(), startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finova_transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Export financial statement as Excel (.xlsx)")
    public ResponseEntity<byte[]> exportExcel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "MONTHLY") String period) {
        byte[] excelData = analyticsService.exportExcelReport(userPrincipal.getId(), period);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finova_financial_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Export formal student financial report as PDF")
    public ResponseEntity<byte[]> exportPdf(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "MONTHLY") String period) {
        byte[] pdfData = analyticsService.exportPdfReport(userPrincipal.getId(), period);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finova_statement.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

}
