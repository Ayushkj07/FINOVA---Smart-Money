package com.finova.integration.google.dto;

public class GoogleDriveUploadResponse {
    private String fileId;
    private String fileName;
    private String fileType;
    private String folderPath;
    private String webLink;
    private Long fileSize;
    private String message;

    public GoogleDriveUploadResponse() {}

    public GoogleDriveUploadResponse(String fileId, String fileName, String fileType, String folderPath, String webLink, Long fileSize, String message) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.folderPath = folderPath;
        this.webLink = webLink;
        this.fileSize = fileSize;
        this.message = message;
    }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFolderPath() { return folderPath; }
    public void setFolderPath(String folderPath) { this.folderPath = folderPath; }

    public String getWebLink() { return webLink; }
    public void setWebLink(String webLink) { this.webLink = webLink; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
