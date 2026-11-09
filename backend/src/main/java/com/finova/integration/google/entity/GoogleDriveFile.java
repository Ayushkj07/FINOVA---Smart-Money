package com.finova.integration.google.entity;

import com.finova.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "google_drive_files")
public class GoogleDriveFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "google_file_id", nullable = false, length = 150)
    private String googleFileId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType; // PDF_REPORT, EXCEL_REPORT, RECEIPT_ATTACHMENT

    @Column(name = "folder_path", length = 255)
    private String folderPath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "web_link", length = 500)
    private String webLink;

    @Column(name = "uploaded_at", updatable = false)
    private Instant uploadedAt = Instant.now();

    public GoogleDriveFile() {}

    public GoogleDriveFile(Long id, User user, String googleFileId, String fileName, String fileType, String folderPath, Long fileSize, String webLink, Instant uploadedAt) {
        this.id = id;
        this.user = user;
        this.googleFileId = googleFileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.folderPath = folderPath;
        this.fileSize = fileSize;
        this.webLink = webLink;
        this.uploadedAt = uploadedAt != null ? uploadedAt : Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public static GoogleDriveFileBuilder builder() {
        return new GoogleDriveFileBuilder();
    }

    public static class GoogleDriveFileBuilder {
        private Long id;
        private User user;
        private String googleFileId;
        private String fileName;
        private String fileType;
        private String folderPath;
        private Long fileSize;
        private String webLink;
        private Instant uploadedAt;

        public GoogleDriveFileBuilder id(Long id) { this.id = id; return this; }
        public GoogleDriveFileBuilder user(User user) { this.user = user; return this; }
        public GoogleDriveFileBuilder googleFileId(String googleFileId) { this.googleFileId = googleFileId; return this; }
        public GoogleDriveFileBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public GoogleDriveFileBuilder fileType(String fileType) { this.fileType = fileType; return this; }
        public GoogleDriveFileBuilder folderPath(String folderPath) { this.folderPath = folderPath; return this; }
        public GoogleDriveFileBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public GoogleDriveFileBuilder webLink(String webLink) { this.webLink = webLink; return this; }
        public GoogleDriveFileBuilder uploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; return this; }

        public GoogleDriveFile build() {
            return new GoogleDriveFile(id, user, googleFileId, fileName, fileType, folderPath, fileSize, webLink, uploadedAt);
        }
    }
}
