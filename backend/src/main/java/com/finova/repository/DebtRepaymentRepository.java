package com.finova.repository;

import com.finova.entity.DebtRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepaymentRepository extends JpaRepository<DebtRepayment, Long> {
    List<DebtRepayment> findByDebtIdOrderByPaymentDateDesc(Long debtId);
}
