package com.finova.service;

import com.finova.dto.features.*;

import java.util.List;
import java.util.Map;

public interface AdvancedFeatureService {
    // Expense Splitter
    List<ExpenseSplitResponse> getAllSplits(Long userId);
    ExpenseSplitResponse createSplit(Long userId, ExpenseSplitRequest request);
    ExpenseSplitResponse markMemberPaid(Long userId, Long splitId, Long memberId);
    ExpenseSplitResponse settleSplit(Long userId, Long splitId);

    // Financial Journal
    List<FinancialJournalResponse> getJournalEntries(Long userId);
    FinancialJournalResponse createJournalEntry(Long userId, FinancialJournalRequest request);
    void deleteJournalEntry(Long userId, Long entryId);

    // Challenges
    List<ChallengeParticipantResponse> getUserChallenges(Long userId);
    ChallengeParticipantResponse joinChallenge(Long userId, Long challengeId);
    ChallengeParticipantResponse updateChallengeProgress(Long userId, Long participantId, java.math.BigDecimal progress);

    // Future Predictor
    ExpensePredictorResponse predictFutureExpenses(Long userId);

    // Calendar
    Map<String, Object> getFinancialCalendarEvents(Long userId, int month, int year);
}
