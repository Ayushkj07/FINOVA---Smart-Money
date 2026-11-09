package com.finova.repository;

import com.finova.entity.FinancialChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialChallengeRepository extends JpaRepository<FinancialChallenge, Long> {
}
