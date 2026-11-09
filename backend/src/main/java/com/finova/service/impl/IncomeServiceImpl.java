package com.finova.service.impl;

import com.finova.dto.income.IncomeRequest;
import com.finova.dto.income.IncomeResponse;
import com.finova.entity.Income;
import com.finova.entity.User;
import com.finova.exception.ResourceNotFoundException;
import com.finova.exception.UnauthorizedException;
import com.finova.repository.IncomeRepository;
import com.finova.repository.UserRepository;
import com.finova.service.IncomeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final com.finova.service.RealtimeSyncService realtimeSyncService;

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getAllIncomes(Long userId) {
        return incomeRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getIncomesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IncomeResponse getIncomeById(Long userId, Long id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        if (!income.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this record");
        }

        return mapToResponse(income);
    }

    @Override
    @Transactional
    public IncomeResponse createIncome(Long userId, IncomeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Income income = Income.builder()
                .user(user)
                .title(request.getTitle().trim())
                .amount(request.getAmount())
                .category(request.getCategory().trim())
                .date(request.getDate())
                .paymentMethod(request.getPaymentMethod())
                .description(request.getDescription())
                .isRecurring(Boolean.TRUE.equals(request.getIsRecurring()))
                .recurrencePeriod(request.getRecurrencePeriod())
                .build();

        IncomeResponse response = mapToResponse(incomeRepository.save(income));
        realtimeSyncService.publishEvent(userId, "INCOME_CREATED", response);
        return response;
    }

    @Override
    @Transactional
    public IncomeResponse updateIncome(Long userId, Long id, IncomeRequest request) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        if (!income.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to edit this record");
        }

        income.setTitle(request.getTitle().trim());
        income.setAmount(request.getAmount());
        income.setCategory(request.getCategory().trim());
        income.setDate(request.getDate());
        income.setPaymentMethod(request.getPaymentMethod());
        income.setDescription(request.getDescription());
        income.setIsRecurring(Boolean.TRUE.equals(request.getIsRecurring()));
        income.setRecurrencePeriod(request.getRecurrencePeriod());

        IncomeResponse response = mapToResponse(incomeRepository.save(income));
        realtimeSyncService.publishEvent(userId, "INCOME_UPDATED", response);
        return response;
    }

    @Override
    @Transactional
    public void deleteIncome(Long userId, Long id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        if (!income.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this record");
        }

        incomeRepository.delete(income);
        realtimeSyncService.publishEvent(userId, "INCOME_DELETED", java.util.Map.of("id", id));
    }

    private IncomeResponse mapToResponse(Income i) {
        return IncomeResponse.builder()
                .id(i.getId())
                .title(i.getTitle())
                .amount(i.getAmount())
                .category(i.getCategory())
                .date(i.getDate())
                .paymentMethod(i.getPaymentMethod())
                .description(i.getDescription())
                .isRecurring(i.getIsRecurring())
                .recurrencePeriod(i.getRecurrencePeriod())
                .createdAt(i.getCreatedAt())
                .build();
    }

    public IncomeServiceImpl(IncomeRepository incomeRepository, UserRepository userRepository, com.finova.service.RealtimeSyncService realtimeSyncService) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
        this.realtimeSyncService = realtimeSyncService;
    }

}
