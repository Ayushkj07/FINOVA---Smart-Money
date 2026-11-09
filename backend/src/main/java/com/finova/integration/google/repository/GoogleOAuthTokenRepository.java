package com.finova.integration.google.repository;

import com.finova.entity.User;
import com.finova.integration.google.entity.GoogleOAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleOAuthTokenRepository extends JpaRepository<GoogleOAuthToken, Long> {
    Optional<GoogleOAuthToken> findByUser(User user);
    Optional<GoogleOAuthToken> findByUserId(Long userId);
    void deleteByUser(User user);
}
