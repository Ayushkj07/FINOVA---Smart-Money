package com.finova.integration.google.repository;

import com.finova.entity.User;
import com.finova.integration.google.entity.GoogleDriveFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoogleDriveFileRepository extends JpaRepository<GoogleDriveFile, Long> {
    List<GoogleDriveFile> findByUserOrderByUploadedAtDesc(User user);
    List<GoogleDriveFile> findByUserIdOrderByUploadedAtDesc(Long userId);
    Optional<GoogleDriveFile> findByGoogleFileId(String googleFileId);
    void deleteByUser(User user);
}
