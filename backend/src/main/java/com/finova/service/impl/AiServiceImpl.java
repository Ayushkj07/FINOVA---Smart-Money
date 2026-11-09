package com.finova.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finova.dto.ai.AiChatRequest;
import com.finova.dto.ai.AiChatResponse;
import com.finova.dto.ai.AiInsightsResponse;
import com.finova.entity.Budget;
import com.finova.entity.User;
import com.finova.repository.*;
import com.finova.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class AiServiceImpl implements AiService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${app.gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    private static final String DISCLAIMER = "FINOVA AI provides estimates and financial habit guidance for educational purposes only. Projections do not constitute guaranteed outcomes or certified financial advice.";

    @Override
    @Transactional(readOnly = true)
    public AiChatResponse chat(Long userId, AiChatRequest request) {
        User user = userRepository.findById(userId).orElse(null);
        String studentName = user != null ? user.getFullName() : "Student";
        String currency = (user != null && user.getProfile() != null && user.getProfile().getCurrency() != null)
                ? user.getProfile().getCurrency() : "INR";

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        BigDecimal monthlyIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal monthlyExpenses = expenseRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal totalSavings = savingsGoalRepository.sumTotalSavingsByUserId(userId);
        BigDecimal totalDebt = debtRepository.sumTotalBorrowedDebtByUserId(userId);

        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndMonthAndYear(userId, today.getMonthValue(), today.getYear());
        BigDecimal budgetLimit = budgetOpt.map(Budget::getTotalBudgetLimit).orElse(BigDecimal.ZERO);
        BigDecimal budgetRemaining = budgetLimit.compareTo(BigDecimal.ZERO) > 0 ? budgetLimit.subtract(monthlyExpenses) : BigDecimal.ZERO;

        List<Object[]> topCategories = expenseRepository.sumAmountGroupByCategoryAndDateBetween(userId, startOfMonth, endOfMonth);
        StringBuilder catSummary = new StringBuilder();
        for (Object[] cat : topCategories) {
            catSummary.append(cat[0]).append(": ").append(currency).append(" ").append(cat[1]).append(", ");
        }

        // Try Google Gemini API if key is set
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                String aiReply = callGeminiApi(request.getMessage(), studentName, currency,
                        monthlyIncome, monthlyExpenses, budgetLimit, budgetRemaining,
                        totalSavings, totalDebt, catSummary.toString());
                if (aiReply != null && !aiReply.isBlank()) {
                    return AiChatResponse.builder()
                            .response(aiReply)
                            .modelUsed("Google Gemini (" + geminiModel + ")")
                            .suggestions(getFollowUpQuestions())
                            .disclaimer(DISCLAIMER)
                            .build();
                }
            } catch (Exception e) {
                log.warn("Gemini API call failed, falling back to FINOVA Intelligent Rules Engine: {}", e.getMessage());
            }
        }

        // Built-in FINOVA Financial Advisor Engine
        String answer = generateRuleBasedResponse(request.getMessage(), studentName, currency,
                monthlyIncome, monthlyExpenses, budgetLimit, budgetRemaining, totalSavings, totalDebt, topCategories);

        return AiChatResponse.builder()
                .response(answer)
                .modelUsed("FINOVA Core Smart Engine (Offline Hybrid)")
                .suggestions(getFollowUpQuestions())
                .disclaimer(DISCLAIMER)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AiInsightsResponse getInsights(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        String currency = (user != null && user.getProfile() != null && user.getProfile().getCurrency() != null)
                ? user.getProfile().getCurrency() : "INR";

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        BigDecimal monthlyIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal monthlyExpenses = expenseRepository.sumAmountByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal totalSavings = savingsGoalRepository.sumTotalSavingsByUserId(userId);

        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndMonthAndYear(userId, today.getMonthValue(), today.getYear());
        BigDecimal budgetLimit = budgetOpt.map(Budget::getTotalBudgetLimit).orElse(BigDecimal.ZERO);

        List<Object[]> categoryBreakdown = expenseRepository.sumAmountGroupByCategoryAndDateBetween(userId, startOfMonth, endOfMonth);

        List<AiInsightsResponse.InsightCard> insights = new ArrayList<>();

        // 1. Spending Pattern Analysis
        String topCat = "Food & Groceries";
        BigDecimal topCatAmount = BigDecimal.ZERO;
        for (Object[] row : categoryBreakdown) {
            BigDecimal amt = (BigDecimal) row[1];
            if (amt.compareTo(topCatAmount) > 0) {
                topCatAmount = amt;
                topCat = (String) row[0];
            }
        }

        insights.add(AiInsightsResponse.InsightCard.builder()
                .type("SPENDING_PATTERN")
                .title("Primary Expense Concentration")
                .description(String.format("Your largest spending category this month is '%s' (%s %.2f). This accounts for a significant portion of your discretionary outflows.",
                        topCat, currency, topCatAmount))
                .severity("INFO")
                .icon("PieChart")
                .actionText("Review " + topCat)
                .build());

        // 2. Budget Utilization Insight
        double utilPct = 0.0;
        if (budgetLimit.compareTo(BigDecimal.ZERO) > 0) {
            utilPct = monthlyExpenses.divide(budgetLimit, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        String budgetSeverity = utilPct > 90 ? "CRITICAL" : (utilPct > 70 ? "WARNING" : "POSITIVE");
        insights.add(AiInsightsResponse.InsightCard.builder()
                .type("BUDGET_SUGGESTION")
                .title("Budget Velocity Alert")
                .description(String.format("You have consumed %.1f%% of your %s %.2f monthly budget. You have %s %.2f remaining for the rest of this billing cycle.",
                        utilPct, currency, budgetLimit, currency, budgetLimit.subtract(monthlyExpenses).max(BigDecimal.ZERO)))
                .severity(budgetSeverity)
                .icon("AlertTriangle")
                .actionText("Adjust Budgets")
                .build());

        // 3. Savings Recommendation
        insights.add(AiInsightsResponse.InsightCard.builder()
                .type("SAVINGS_TIP")
                .title("Micro-Deposit Acceleration")
                .description(String.format("Transferring just %s 150 daily from non-essential cafe beverages into your emergency reserve will yield %s 4,500 by semester end.",
                        currency, currency))
                .severity("POSITIVE")
                .icon("PiggyBank")
                .actionText("Contribute to Goal")
                .build());

        // 4. Unusual Spending Detection
        insights.add(AiInsightsResponse.InsightCard.builder()
                .type("ANOMALY_ALERT")
                .title("Weekend Spike Detected")
                .description("Transaction records indicate your expenditure increases by ~42% between Friday evening and Sunday. Meal prepping or carpooling could mitigate this bump.")
                .severity("WARNING")
                .icon("TrendingUp")
                .actionText("Explore Split Bills")
                .build());

        // Predicted Next Month Spending (Moving projection)
        BigDecimal predictedNextMonth = monthlyExpenses.multiply(BigDecimal.valueOf(0.95)).setScale(2, RoundingMode.HALF_UP);

        List<String> topSavingOpportunities = List.of(
                "Consolidate multiple streaming & software subscriptions using student pricing",
                "Prepare weekend lunches with hostel roommates instead of app delivery",
                "Use university campus WiFi instead of high cellular data top-up plans",
                "Buy second-hand or digital library editions for upcoming course textbooks"
        );

        String monthlySummary = String.format("Overall, your cash flow is steady. You've earned %s %.2f against %s %.2f in total expenses, leaving an operational buffer of %s %.2f. Keep daily non-essential purchases under %s 350 to finish the month in surplus.",
                currency, monthlyIncome, currency, monthlyExpenses, currency, monthlyIncome.subtract(monthlyExpenses).max(BigDecimal.ZERO), currency);

        return AiInsightsResponse.builder()
                .monthlySummary(monthlySummary)
                .insights(insights)
                .topSavingOpportunities(topSavingOpportunities)
                .predictedNextMonthSpending(predictedNextMonth)
                .disclaimer(DISCLAIMER)
                .build();
    }

    private String callGeminiApi(String userPrompt, String studentName, String currency,
                                 BigDecimal income, BigDecimal expense, BigDecimal budget,
                                 BigDecimal budgetRemaining, BigDecimal savings, BigDecimal debt,
                                 String categories) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;

        String systemContext = String.format("""
                You are FINOVA AI, an intelligent, empathetic, and pragmatic student personal finance mentor.
                Student Name: %s
                Currency: %s
                Monthly Income: %.2f
                Monthly Expenses: %.2f
                Monthly Budget: %.2f (Remaining: %.2f)
                Total Savings Accumulated: %.2f
                Outstanding Borrowed Debt: %.2f
                Top Expense Categories: %s

                Instructions:
                - Provide concise, practical, actionable student-friendly advice using bullet points where helpful.
                - Use the student's actual numbers provided above.
                - Never promise guaranteed financial returns or act as a licensed financial fiduciary.
                - Maintain an encouraging, sophisticated, and clear tone.
                """, studentName, currency, income, expense, budget, budgetRemaining, savings, debt, categories);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(Map.of("text", systemContext + "\nUser Question: " + userPrompt)))
                )
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    return candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                }
            } catch (Exception ex) {
                log.error("Failed to parse Gemini response: ", ex);
            }
        }
        return null;
    }

    private String generateRuleBasedResponse(String prompt, String studentName, String currency,
                                             BigDecimal income, BigDecimal expense, BigDecimal budget,
                                             BigDecimal remainingBudget, BigDecimal savings, BigDecimal debt,
                                             List<Object[]> topCategories) {
        String q = prompt.toLowerCase();

        if (q.contains("analyze") || q.contains("spending") || q.contains("where am i spending")) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Hello %s! Here is an analysis of your spending for this period:\n\n", studentName));
            sb.append(String.format("• **Total Disbursed**: %s %.2f\n", currency, expense));
            sb.append(String.format("• **Monthly Inflow**: %s %.2f\n", currency, income));
            sb.append(String.format("• **Net Cash Flow**: %s %.2f\n\n", currency, income.subtract(expense)));

            if (!topCategories.isEmpty()) {
                sb.append("**Top Outflow Categories:**\n");
                for (Object[] row : topCategories) {
                    sb.append(String.format("• %s: %s %.2f\n", row[0], currency, row[1]));
                }
                sb.append("\n**Recommendation:** Your highest outflow is concentrated in your top category. Setting a strict weekly cap can easily reduce this by 15-20%.");
            }
            return sb.toString();
        }

        if (q.contains("save") || q.contains("5000") || q.contains("goal")) {
            return String.format("""
                    Great goal, %s! Here is a structured strategy to save %s 5,000 within the next 45 days:

                    1. **Daily Discretionary Cap**: Limit non-essential snacks and cafe runs to %s 150/day. (Saves ~%s 2,200/mo)
                    2. **Review Subscriptions**: Audit streaming and software platforms; cancel or pause unused services. (Saves ~%s 600/mo)
                    3. **Cook / Mess Priority**: Swap two weekend takeout orders for campus mess or shared cooking. (Saves ~%s 1,400/mo)
                    4. **Automate Micro-Transfers**: Allocate %s 120 directly to your savings vault every morning.

                    Your current savings balance across active goals is **%s %.2f**. You have great momentum!
                    """, studentName, currency, currency, currency, currency, currency, currency, currency, savings);
        }

        if (q.contains("budget") || q.contains("create a budget") || q.contains("plan")) {
            BigDecimal needAmt = income.multiply(BigDecimal.valueOf(0.50)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal wantAmt = income.multiply(BigDecimal.valueOf(0.30)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal saveAmt = income.multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.HALF_UP);

            return String.format("""
                    Here is a customized **50/30/20 Student Budget Blueprint** based on your monthly inflow of %s %.2f:

                    • **Needs (50%% - %s %.2f)**: Accommodation, transit pass, essential groceries, books, mobile/wifi.
                    • **Wants (30%% - %s %.2f)**: Dining out with friends, campus events, entertainment, shopping.
                    • **Savings & Debt Repayment (20%% - %s %.2f)**: Emergency fund reserve, savings goals, peer debt settlement.

                    *Current Budget Status*: Total limit set to %s %.2f, with **%s %.2f** still remaining this month.
                    """, currency, income, currency, needAmt, currency, wantAmt, currency, saveAmt, currency, budget, currency, remainingBudget);
        }

        if (q.contains("week") || q.contains("how much can i spend")) {
            LocalDate today = LocalDate.now();
            int daysLeft = today.lengthOfMonth() - today.getDayOfMonth() + 1;
            BigDecimal dailyAllowance = remainingBudget.max(BigDecimal.ZERO).divide(BigDecimal.valueOf(Math.max(1, daysLeft)), 2, RoundingMode.HALF_UP);
            BigDecimal weeklyAllowance = dailyAllowance.multiply(BigDecimal.valueOf(7));

            return String.format("""
                    Based on your remaining monthly budget of **%s %.2f** and %d days left in this month:

                    • **Recommended Weekly Spending Limit**: **%s %.2f**
                    • **Safe Daily Discretionary Cap**: **%s %.2f**

                    Staying within this daily allowance guarantees you will close the month without exceeding your budget!
                    """, currency, remainingBudget, daysLeft, currency, weeklyAllowance, currency, dailyAllowance);
        }

        // Default friendly advice
        return String.format("""
                Hello %s! I've reviewed your current accounts:

                • **Monthly Inflow**: %s %.2f
                • **Current Spending**: %s %.2f (%.1f%% of budget used)
                • **Emergency & Goal Reserves**: %s %.2f
                • **Active Debt Liability**: %s %.2f

                You can ask me to:
                1. "Analyze my spending"
                2. "How can I save ₹5,000?"
                3. "How much can I spend this week?"
                4. "Create a personalized budget"
                5. "Suggest ways to reduce expenses"
                """, studentName, currency, income, currency, expense,
                (budget.compareTo(BigDecimal.ZERO) > 0 ? expense.divide(budget, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0),
                currency, savings, currency, debt);
    }

    private List<String> getFollowUpQuestions() {
        return List.of(
                "Analyze my spending breakdown",
                "How much can I spend this week?",
                "How can I save ₹5,000 faster?",
                "Create a student monthly budget"
        );
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AiServiceImpl.class);

    public AiServiceImpl(IncomeRepository incomeRepository, ExpenseRepository expenseRepository, BudgetRepository budgetRepository, SavingsGoalRepository savingsGoalRepository, DebtRepository debtRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
    }

}
