package com.finova.controller;

import com.finova.dto.features.*;
import com.finova.security.UserPrincipal;
import com.finova.service.AdvancedFeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/features")
@Tag(name = "Advanced Features", description = "Split bills, Financial Journal, Savings Challenges, Predictor, Calendar")
public class AdvancedFeaturesController {

    private final AdvancedFeatureService advancedFeatureService;

    // --- EXPENSE SPLITTER ---
    @GetMapping("/splits")
    @Operation(summary = "Get all group expense splits")
    public ResponseEntity<List<ExpenseSplitResponse>> getAllSplits(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(advancedFeatureService.getAllSplits(userPrincipal.getId()));
    }

    @PostMapping("/splits")
    @Operation(summary = "Create an expense split with friends")
    public ResponseEntity<ExpenseSplitResponse> createSplit(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ExpenseSplitRequest request) {
        return new ResponseEntity<>(advancedFeatureService.createSplit(userPrincipal.getId(), request), HttpStatus.CREATED);
    }

    @PatchMapping("/splits/{splitId}/members/{memberId}/paid")
    @Operation(summary = "Mark a member's split portion as paid")
    public ResponseEntity<ExpenseSplitResponse> markMemberPaid(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long splitId,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(advancedFeatureService.markMemberPaid(userPrincipal.getId(), splitId, memberId));
    }

    @PatchMapping("/splits/{splitId}/settle")
    @Operation(summary = "Mark an entire split as settled")
    public ResponseEntity<ExpenseSplitResponse> settleSplit(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long splitId) {
        return ResponseEntity.ok(advancedFeatureService.settleSplit(userPrincipal.getId(), splitId));
    }

    // --- FINANCIAL JOURNAL ---
    @GetMapping("/journal")
    @Operation(summary = "Get student financial journal entries")
    public ResponseEntity<List<FinancialJournalResponse>> getJournalEntries(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(advancedFeatureService.getJournalEntries(userPrincipal.getId()));
    }

    @PostMapping("/journal")
    @Operation(summary = "Create a financial reflection note or milestone")
    public ResponseEntity<FinancialJournalResponse> createJournalEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FinancialJournalRequest request) {
        return new ResponseEntity<>(advancedFeatureService.createJournalEntry(userPrincipal.getId(), request), HttpStatus.CREATED);
    }

    @DeleteMapping("/journal/{id}")
    @Operation(summary = "Delete a journal entry")
    public ResponseEntity<Void> deleteJournalEntry(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        advancedFeatureService.deleteJournalEntry(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    // --- SAVINGS CHALLENGES ---
    @GetMapping("/challenges")
    @Operation(summary = "Get user's active savings sprint challenges")
    public ResponseEntity<List<ChallengeParticipantResponse>> getUserChallenges(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(advancedFeatureService.getUserChallenges(userPrincipal.getId()));
    }

    @PostMapping("/challenges/{challengeId}/join")
    @Operation(summary = "Enroll in a savings sprint challenge")
    public ResponseEntity<ChallengeParticipantResponse> joinChallenge(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long challengeId) {
        return ResponseEntity.ok(advancedFeatureService.joinChallenge(userPrincipal.getId(), challengeId));
    }

    @PostMapping("/challenges/participants/{participantId}/progress")
    @Operation(summary = "Log progress toward challenge")
    public ResponseEntity<ChallengeParticipantResponse> updateChallengeProgress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long participantId,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(advancedFeatureService.updateChallengeProgress(userPrincipal.getId(), participantId, amount));
    }

    // --- FUTURE EXPENSE PREDICTOR ---
    @GetMapping("/predictor")
    @Operation(summary = "Predict future monthly and category spending based on historical trend")
    public ResponseEntity<ExpensePredictorResponse> getPredictions(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(advancedFeatureService.predictFutureExpenses(userPrincipal.getId()));
    }

    // --- FINANCIAL CALENDAR ---
    @GetMapping("/calendar")
    @Operation(summary = "Get calendar schedule of upcoming payments, renewals, debt due dates, and milestones")
    public ResponseEntity<Map<String, Object>> getCalendarEvents(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "11") int month,
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(advancedFeatureService.getFinancialCalendarEvents(userPrincipal.getId(), month, year));
    }

    public AdvancedFeaturesController(AdvancedFeatureService advancedFeatureService) {
        this.advancedFeatureService = advancedFeatureService;
    }

}
