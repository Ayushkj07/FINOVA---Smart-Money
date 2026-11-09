package com.finova.integration.google.repository;

import com.finova.entity.User;
import com.finova.integration.google.entity.GoogleAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleAccountRepository extends JpaRepository<GoogleAccount, Long> {
    Optional<GoogleAccount> findByUser(User user);
    Optional<GoogleAccount> findByUserId(Long userId);
    Optional<GoogleAccount> findByGoogleUserId(String googleUserId);
    Optional<GoogleAccount> findByGoogleEmail(String googleEmail);
    boolean existsByGoogleEmail(String googleEmail);
    void deleteByUser(User user);
}
