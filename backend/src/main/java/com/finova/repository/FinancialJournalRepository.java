package com.finova.repository;

import com.finova.entity.FinancialJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialJournalRepository extends JpaRepository<FinancialJournal, Long> {
    List<FinancialJournal> findByUserIdOrderByEntryDateDesc(Long userId);
}
