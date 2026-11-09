package com.finova.repository;

import com.finova.entity.ExpenseSplitMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitMemberRepository extends JpaRepository<ExpenseSplitMember, Long> {
    List<ExpenseSplitMember> findByExpenseSplitId(Long expenseSplitId);
}
