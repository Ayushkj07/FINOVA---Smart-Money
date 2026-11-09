package com.finova.integration.google.dto;

import java.time.Instant;

public class GoogleDriveFileDto {
    private Long id;
    private String googleFileId;
    private String fileName;
    private String fileType;
    private String folderPath;
    private Long fileSize;
    private String webLink;
    private Instant uploadedAt;

    public GoogleDriveFileDto() {}

    public GoogleDriveFileDto(Long id, String googleFileId, String fileName, String fileType, String folderPath, Long fileSize, String webLink, Instant uploadedAt) {
        this.id = id;
        this.googleFileId = googleFileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.folderPath = folderPath;
        this.fileSize = fileSize;
        this.webLink = webLink;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGoogleFileId() { return googleFileId; }
    public void setGoogleFileId(String googleFileId) { this.googleFileId = googleFileId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFolderPath() { return folderPath; }
    public void setFolderPath(String folderPath) { this.folderPath = folderPath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getWebLink() { return webLink; }
    public void setWebLink(String webLink) { this.webLink = webLink; }

    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
}
