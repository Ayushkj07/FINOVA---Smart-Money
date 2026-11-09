package com.finova.repository;

import com.finova.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    List<ExpenseCategory> findByIsSystemTrueOrUserId(Long userId);
    List<ExpenseCategory> findByIsSystemTrue();
}
