package com.finova.integration.google.repository;

import com.finova.entity.User;
import com.finova.integration.google.entity.GoogleSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoogleSyncLogRepository extends JpaRepository<GoogleSyncLog, Long> {
    List<GoogleSyncLog> findByUserOrderBySyncedAtDesc(User user);
    List<GoogleSyncLog> findByUserIdOrderBySyncedAtDesc(Long userId);
    List<GoogleSyncLog> findTop20ByUserIdOrderBySyncedAtDesc(Long userId);
    void deleteByUser(User user);
}
