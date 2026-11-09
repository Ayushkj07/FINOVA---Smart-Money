package com.finova.integration.google.service;

import com.finova.integration.google.dto.GoogleDriveFileDto;
import com.finova.integration.google.dto.GoogleDriveUploadResponse;

import java.util.List;

public interface GoogleDriveService {
    GoogleDriveUploadResponse uploadGeneratedReport(Long userId, String reportType, String fileFormat);
    List<GoogleDriveFileDto> listFiles(Long userId);
    void deleteFile(Long userId, Long fileRecordId);
}
