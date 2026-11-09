package com.finova.service.impl;

import com.finova.dto.debt.DebtRepaymentRequest;
import com.finova.dto.debt.DebtRequest;
import com.finova.dto.debt.DebtResponse;
import com.finova.entity.Debt;
import com.finova.entity.DebtRepayment;
import com.finova.entity.User;
import com.finova.entity.enums.DebtStatus;
import com.finova.exception.BadRequestException;
import com.finova.exception.ResourceNotFoundException;
import com.finova.exception.UnauthorizedException;
import com.finova.repository.DebtRepaymentRepository;
import com.finova.repository.DebtRepository;
import com.finova.repository.UserRepository;
import com.finova.service.DebtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final DebtRepaymentRepository debtRepaymentRepository;
    private final UserRepository userRepository;
    private final com.finova.service.RealtimeSyncService realtimeSyncService;

    @Override
    @Transactional(readOnly = true)
    public List<DebtResponse> getAllDebts(Long userId) {
        return debtRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DebtResponse getDebtById(Long userId, Long id) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debt record not found with id: " + id));

        if (!debt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this debt record");
        }

        return mapToResponse(debt);
    }

    @Override
    @Transactional
    public DebtResponse createDebt(Long userId, DebtRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Debt debt = Debt.builder()
                .user(user)
                .type(request.getType())
                .counterpartyName(request.getCounterpartyName().trim())
                .counterpartyContact(request.getCounterpartyContact())
                .initialAmount(request.getInitialAmount())
                .outstandingBalance(request.getInitialAmount())
                .interestRate(request.getInterestRate() != null ? request.getInterestRate() : BigDecimal.ZERO)
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .status(DebtStatus.ACTIVE)
                .build();

        DebtResponse response = mapToResponse(debtRepository.save(debt));
        realtimeSyncService.publishEvent(userId, "DEBT_UPDATED", response);
        return response;
    }

    @Override
    @Transactional
    public DebtResponse updateDebt(Long userId, Long id, DebtRequest request) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debt record not found with id: " + id));

        if (!debt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to edit this debt record");
        }

        debt.setType(request.getType());
        debt.setCounterpartyName(request.getCounterpartyName().trim());
        debt.setCounterpartyContact(request.getCounterpartyContact());
        debt.setDueDate(request.getDueDate());
        debt.setDescription(request.getDescription());
        if (request.getInterestRate() != null) debt.setInterestRate(request.getInterestRate());

        DebtResponse response = mapToResponse(debtRepository.save(debt));
        realtimeSyncService.publishEvent(userId, "DEBT_UPDATED", response);
        return response;
    }

    @Override
    @Transactional
    public void deleteDebt(Long userId, Long id) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debt record not found with id: " + id));

        if (!debt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this debt record");
        }

        debtRepository.delete(debt);
        realtimeSyncService.publishEvent(userId, "DEBT_DELETED", java.util.Map.of("id", id));
    }

    @Override
    @Transactional
    public DebtResponse addRepayment(Long userId, Long debtId, DebtRepaymentRequest request) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt record not found with id: " + debtId));

        if (!debt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to repay this debt");
        }

        if (debt.getStatus() == DebtStatus.PAID) {
            throw new BadRequestException("This debt is already fully paid.");
        }

        BigDecimal paymentAmount = request.getAmount();
        BigDecimal newOutstanding = debt.getOutstandingBalance().subtract(paymentAmount);

        if (newOutstanding.compareTo(BigDecimal.ZERO) <= 0) {
            debt.setOutstandingBalance(BigDecimal.ZERO);
            debt.setStatus(DebtStatus.PAID);
        } else {
            debt.setOutstandingBalance(newOutstanding);
        }

        DebtRepayment repayment = DebtRepayment.builder()
                .debt(debt)
                .amount(paymentAmount)
                .paymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now())
                .notes(request.getNotes())
                .build();

        debtRepaymentRepository.save(repayment);
        DebtResponse response = mapToResponse(debtRepository.save(debt));
        realtimeSyncService.publishEvent(userId, "DEBT_UPDATED", response);
        return response;
    }

    private DebtResponse mapToResponse(Debt d) {
        BigDecimal totalPaid = d.getInitialAmount().subtract(d.getOutstandingBalance()).max(BigDecimal.ZERO);

        // Check if overdue
        if (d.getStatus() != DebtStatus.PAID && d.getDueDate() != null && d.getDueDate().isBefore(LocalDate.now())) {
            d.setStatus(DebtStatus.OVERDUE);
        }

        List<DebtResponse.RepaymentDto> repaymentDtos = debtRepaymentRepository
                .findByDebtIdOrderByPaymentDateDesc(d.getId())
                .stream()
                .map(r -> DebtResponse.RepaymentDto.builder()
                        .id(r.getId())
                        .amount(r.getAmount())
                        .paymentDate(r.getPaymentDate())
                        .notes(r.getNotes())
                        .build())
                .collect(Collectors.toList());

        return DebtResponse.builder()
                .id(d.getId())
                .type(d.getType())
                .counterpartyName(d.getCounterpartyName())
                .counterpartyContact(d.getCounterpartyContact())
                .initialAmount(d.getInitialAmount())
                .outstandingBalance(d.getOutstandingBalance())
                .totalPaid(totalPaid)
                .interestRate(d.getInterestRate())
                .dueDate(d.getDueDate())
                .description(d.getDescription())
                .status(d.getStatus())
                .repayments(repaymentDtos)
                .build();
    }

    public DebtServiceImpl(DebtRepository debtRepository, DebtRepaymentRepository debtRepaymentRepository, UserRepository userRepository, com.finova.service.RealtimeSyncService realtimeSyncService) {
        this.debtRepository = debtRepository;
        this.debtRepaymentRepository = debtRepaymentRepository;
        this.userRepository = userRepository;
        this.realtimeSyncService = realtimeSyncService;
    }

}
