package com.finova.integration.google.service;

import com.finova.integration.google.dto.GoogleSheetsExportResponse;

public interface GoogleSheetsService {
    GoogleSheetsExportResponse exportFullFinancialData(Long userId, String customTitle);
}
