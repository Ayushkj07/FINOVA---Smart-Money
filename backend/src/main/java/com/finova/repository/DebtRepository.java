package com.finova.repository;

import com.finova.entity.Debt;
import com.finova.entity.enums.DebtStatus;
import com.finova.entity.enums.DebtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Debt> findByUserIdAndType(Long userId, DebtType type);

    @Query("SELECT COALESCE(SUM(d.outstandingBalance), 0) FROM Debt d WHERE d.user.id = :userId AND d.type = 'BORROWED' AND d.status <> 'PAID'")
    BigDecimal sumTotalBorrowedDebtByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(d.outstandingBalance), 0) FROM Debt d WHERE d.user.id = :userId AND d.type = 'LENT' AND d.status <> 'PAID'")
    BigDecimal sumTotalLentAmountByUserId(@Param("userId") Long userId);
}
