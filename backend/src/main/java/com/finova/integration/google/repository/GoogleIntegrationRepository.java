package com.finova.integration.google.repository;

import com.finova.entity.User;
import com.finova.integration.google.entity.GoogleIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoogleIntegrationRepository extends JpaRepository<GoogleIntegration, Long> {
    List<GoogleIntegration> findByUser(User user);
    List<GoogleIntegration> findByUserId(Long userId);
    Optional<GoogleIntegration> findByUserAndServiceName(User user, String serviceName);
    Optional<GoogleIntegration> findByUserIdAndServiceName(Long userId, String serviceName);
    void deleteByUser(User user);
    void deleteByUserAndServiceName(User user, String serviceName);
}
