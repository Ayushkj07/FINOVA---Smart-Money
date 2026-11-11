package com.finova.repository;

import com.finova.entity.LoginOtpChallenge;
import com.finova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginOtpChallengeRepository extends JpaRepository<LoginOtpChallenge, Long> {
    Optional<LoginOtpChallenge> findByChallengeId(String challengeId);
    List<LoginOtpChallenge> findByUserAndUsedAtIsNull(User user);
    List<LoginOtpChallenge> findByEmailAndUsedAtIsNull(String email);
    Optional<LoginOtpChallenge> findFirstByEmailAndUsedAtIsNullOrderByCreatedAtDesc(String email);
    void deleteByUser(User user);
}
