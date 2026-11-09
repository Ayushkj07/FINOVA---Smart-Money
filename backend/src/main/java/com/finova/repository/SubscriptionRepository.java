package com.finova.repository;

import com.finova.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserIdOrderByNextRenewalDateAsc(Long userId);
    List<Subscription> findByUserIdAndIsActiveTrueOrderByNextRenewalDateAsc(Long userId);
    List<Subscription> findByIsActiveTrueAndNextRenewalDateLessThanEqual(LocalDate date);

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Subscription s WHERE s.user.id = :userId AND s.isActive = true")
    BigDecimal sumMonthlyActiveSubscriptions(@Param("userId") Long userId);
}
