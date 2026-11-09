package com.finova.config;

import com.finova.entity.*;
import com.finova.entity.enums.*;
import com.finova.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final SavingsContributionRepository savingsContributionRepository;
    private final DebtRepository debtRepository;
    private final DebtRepaymentRepository debtRepaymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final FinancialChallengeRepository financialChallengeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final FinancialJournalRepository financialJournalRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing FINOVA default seed data...");

        // 1. Roles
        Role studentRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_STUDENT)
                        .description("Student account")
                        .build()));

        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_ADMIN)
                        .description("Platform administrator")
                        .build()));

        // 2. System Expense Categories
        if (expenseCategoryRepository.count() == 0) {
            List<ExpenseCategory> categories = List.of(
                    ExpenseCategory.builder().name("Food & Groceries").icon("Utensils").colorHex("#9B87F5").isSystem(true).build(),
                    ExpenseCategory.builder().name("Transportation").icon("Bus").colorHex("#A8C9B5").isSystem(true).build(),
                    ExpenseCategory.builder().name("Education & Books").icon("BookOpen").colorHex("#65A982").isSystem(true).build(),
                    ExpenseCategory.builder().name("Shopping").icon("ShoppingBag").colorHex("#E8B96B").isSystem(true).build(),
                    ExpenseCategory.builder().name("Entertainment").icon("Film").colorHex("#DF858B").isSystem(true).build(),
                    ExpenseCategory.builder().name("Accommodation & Rent").icon("Home").colorHex("#7B64EB").isSystem(true).build(),
                    ExpenseCategory.builder().name("Healthcare & Medical").icon("HeartPulse").colorHex("#E57373").isSystem(true).build(),
                    ExpenseCategory.builder().name("Mobile & Internet").icon("Wifi").colorHex("#4DD0E1").isSystem(true).build(),
                    ExpenseCategory.builder().name("Subscriptions").icon("Repeat").colorHex("#BA68C8").isSystem(true).build(),
                    ExpenseCategory.builder().name("Personal Care").icon("Smile").colorHex("#81C784").isSystem(true).build(),
                    ExpenseCategory.builder().name("Other & Miscellaneous").icon("MoreHorizontal").colorHex("#90A4AE").isSystem(true).build()
            );
            expenseCategoryRepository.saveAll(categories);
        }

        // 3. Demo Admin User
        if (!userRepository.existsByEmail("admin@finova.com")) {
            User admin = User.builder()
                    .email("admin@finova.com")
                    .fullName("FINOVA Admin")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .isActive(true)
                    .isVerified(true)
                    .roles(Set.of(adminRole))
                    .build();
            admin = userRepository.save(admin);

            UserProfile adminProfile = UserProfile.builder()
                    .user(admin)
                    .collegeName("FINOVA Operations")
                    .studentStatus("Administrator")
                    .currency("INR")
                    .timezone("Asia/Kolkata")
                    .build();
            userProfileRepository.save(adminProfile);
        }

        // 4. Demo Student User (Alex Morgan)
        if (!userRepository.existsByEmail("student@finova.com")) {
            User student = User.builder()
                    .email("student@finova.com")
                    .fullName("Alex Morgan")
                    .password(passwordEncoder.encode("Student@1234"))
                    .isActive(true)
                    .isVerified(true)
                    .roles(Set.of(studentRole))
                    .build();
            student = userRepository.save(student);

            UserProfile studentProfile = UserProfile.builder()
                    .user(student)
                    .collegeName("Stanford University")
                    .studentStatus("Undergraduate - Year 3")
                    .currency("INR")
                    .monthlyAllowance(BigDecimal.valueOf(15000.00))
                    .financialGoalSummary("Saving for MacBook Pro M3 and building a ₹25,000 emergency fund.")
                    .timezone("Asia/Kolkata")
                    .build();
            userProfileRepository.save(studentProfile);

            LocalDate today = LocalDate.now();

            // Incomes
            List<Income> incomes = List.of(
                    Income.builder().user(student).title("Monthly Family Allowance").amount(BigDecimal.valueOf(15000.00)).category("Family Allowance").date(today.withDayOfMonth(1)).paymentMethod(PaymentMethod.BANK_TRANSFER).description("Monthly pocket allowance from parents").isRecurring(true).recurrencePeriod(RecurrencePeriod.MONTHLY).build(),
                    Income.builder().user(student).title("CS Lab Teaching Assistant").amount(BigDecimal.valueOf(6500.00)).category("Part-time Job").date(today.withDayOfMonth(3)).paymentMethod(PaymentMethod.BANK_TRANSFER).description("Bi-weekly TA stipend for CS106A").isRecurring(true).recurrencePeriod(RecurrencePeriod.MONTHLY).build(),
                    Income.builder().user(student).title("Freelance Landing Page Build").amount(BigDecimal.valueOf(8000.00)).category("Freelancing").date(today.withDayOfMonth(8)).paymentMethod(PaymentMethod.UPI).description("Client payment for React Tailwind website").isRecurring(false).recurrencePeriod(RecurrencePeriod.NONE).build(),
                    Income.builder().user(student).title("Merit Scholarship Disbursement").amount(BigDecimal.valueOf(5000.00)).category("Scholarship").date(today.minusMonths(1).withDayOfMonth(15)).paymentMethod(PaymentMethod.BANK_TRANSFER).description("Academic semester scholarship credit").isRecurring(false).recurrencePeriod(RecurrencePeriod.NONE).build()
            );
            incomeRepository.saveAll(incomes);

            // Expenses
            List<Expense> expenses = List.of(
                    Expense.builder().user(student).categoryName("Food & Groceries").title("Weekly Supermarket Run").amount(BigDecimal.valueOf(2450.00)).date(today.withDayOfMonth(2)).paymentMethod(PaymentMethod.UPI).description("Oats, milk, fruits, veggies, bread").build(),
                    Expense.builder().user(student).categoryName("Transportation").title("Monthly Campus Metro Pass").amount(BigDecimal.valueOf(850.00)).date(today.withDayOfMonth(3)).paymentMethod(PaymentMethod.DEBIT_CARD).description("Subway metro recharge for campus commute").build(),
                    Expense.builder().user(student).categoryName("Education & Books").title("Algorithms Textbook & Notebooks").amount(BigDecimal.valueOf(1420.00)).date(today.withDayOfMonth(4)).paymentMethod(PaymentMethod.UPI).description("CLRS reference guide and stationery").build(),
                    Expense.builder().user(student).categoryName("Mobile & Internet").title("Hostel Fiber WiFi Share").amount(BigDecimal.valueOf(600.00)).date(today.withDayOfMonth(5)).paymentMethod(PaymentMethod.UPI).description("Split bill for 200mbps broadband").build(),
                    Expense.builder().user(student).categoryName("Entertainment").title("Weekend Movie Night").amount(BigDecimal.valueOf(480.00)).date(today.withDayOfMonth(6)).paymentMethod(PaymentMethod.UPI).description("Cinema ticket and popcorn with friends").build(),
                    Expense.builder().user(student).categoryName("Food & Groceries").title("Campus Cafe Study Coffee").amount(BigDecimal.valueOf(260.00)).date(today.withDayOfMonth(7)).paymentMethod(PaymentMethod.UPI).description("Iced latte and bagel during study group").build(),
                    Expense.builder().user(student).categoryName("Subscriptions").title("Spotify Student Plan").amount(BigDecimal.valueOf(59.00)).date(today.withDayOfMonth(8)).paymentMethod(PaymentMethod.UPI).description("Monthly music streaming").isRecurring(true).recurrencePeriod(RecurrencePeriod.MONTHLY).build(),
                    Expense.builder().user(student).categoryName("Personal Care").title("Haircut & Grooming").amount(BigDecimal.valueOf(350.00)).date(today.withDayOfMonth(9)).paymentMethod(PaymentMethod.CASH).description("Campus salon haircut").build(),
                    Expense.builder().user(student).categoryName("Food & Groceries").title("Hostel Dinner Mess Delivery").amount(BigDecimal.valueOf(380.00)).date(today.withDayOfMonth(10)).paymentMethod(PaymentMethod.UPI).description("Late night study meal").build()
            );
            expenseRepository.saveAll(expenses);

            // Monthly Budget
            Budget budget = Budget.builder()
                    .user(student)
                    .name("Monthly Student Budget - " + today.getMonth().name())
                    .month(today.getMonthValue())
                    .year(today.getYear())
                    .totalBudgetLimit(BigDecimal.valueOf(20000.00))
                    .build();
            budget = budgetRepository.save(budget);

            List<BudgetCategory> budgetCats = List.of(
                    BudgetCategory.builder().budget(budget).categoryName("Food & Groceries").limitAmount(BigDecimal.valueOf(6500.00)).spentAmount(BigDecimal.valueOf(3090.00)).build(),
                    BudgetCategory.builder().budget(budget).categoryName("Transportation").limitAmount(BigDecimal.valueOf(2000.00)).spentAmount(BigDecimal.valueOf(850.00)).build(),
                    BudgetCategory.builder().budget(budget).categoryName("Education & Books").limitAmount(BigDecimal.valueOf(4000.00)).spentAmount(BigDecimal.valueOf(1420.00)).build(),
                    BudgetCategory.builder().budget(budget).categoryName("Entertainment").limitAmount(BigDecimal.valueOf(3000.00)).spentAmount(BigDecimal.valueOf(480.00)).build(),
                    BudgetCategory.builder().budget(budget).categoryName("Subscriptions").limitAmount(BigDecimal.valueOf(1500.00)).spentAmount(BigDecimal.valueOf(59.00)).build(),
                    BudgetCategory.builder().budget(budget).categoryName("Mobile & Internet").limitAmount(BigDecimal.valueOf(1000.00)).spentAmount(BigDecimal.valueOf(600.00)).build(),
                    BudgetCategory.builder().budget(budget).categoryName("Personal Care").limitAmount(BigDecimal.valueOf(2000.00)).spentAmount(BigDecimal.valueOf(350.00)).build()
            );
            budgetCategoryRepository.saveAll(budgetCats);

            // Savings Goals
            SavingsGoal g1 = SavingsGoal.builder()
                    .user(student)
                    .name("Apple MacBook Air M3")
                    .targetAmount(BigDecimal.valueOf(95000.00))
                    .currentAmount(BigDecimal.valueOf(42500.00))
                    .targetDate(today.plusMonths(4))
                    .category("TECH")
                    .description("Upgrading my laptop for machine learning coursework and freelance dev.")
                    .status("IN_PROGRESS")
                    .build();
            g1 = savingsGoalRepository.save(g1);

            savingsContributionRepository.save(SavingsContribution.builder()
                    .savingsGoal(g1)
                    .user(student)
                    .amount(BigDecimal.valueOf(5000.00))
                    .contributionDate(today.minusDays(5))
                    .notes("Saved from freelance project")
                    .build());

            SavingsGoal g2 = SavingsGoal.builder()
                    .user(student)
                    .name("Semester Break Beach Trip")
                    .targetAmount(BigDecimal.valueOf(12000.00))
                    .currentAmount(BigDecimal.valueOf(8400.00))
                    .targetDate(today.plusMonths(2))
                    .category("TRAVEL")
                    .description("Goa trip with hostel roommates after final examinations.")
                    .status("IN_PROGRESS")
                    .build();
            savingsGoalRepository.save(g2);

            SavingsGoal g3 = SavingsGoal.builder()
                    .user(student)
                    .name("Emergency Rainy Day Fund")
                    .targetAmount(BigDecimal.valueOf(25000.00))
                    .currentAmount(BigDecimal.valueOf(15000.00))
                    .targetDate(today.plusMonths(6))
                    .category("EMERGENCY")
                    .description("Safety cushion for unexpected healthcare or travel needs.")
                    .status("IN_PROGRESS")
                    .build();
            savingsGoalRepository.save(g3);

            // Debts
            Debt d1 = Debt.builder()
                    .user(student)
                    .type(DebtType.BORROWED)
                    .counterpartyName("Liam (Brother)")
                    .counterpartyContact("+1-555-0192")
                    .initialAmount(BigDecimal.valueOf(3000.00))
                    .outstandingBalance(BigDecimal.valueOf(1500.00))
                    .interestRate(BigDecimal.ZERO)
                    .dueDate(today.plusDays(20))
                    .description("Borrowed for semester lab fee deposit")
                    .status(DebtStatus.ACTIVE)
                    .build();
            d1 = debtRepository.save(d1);

            debtRepaymentRepository.save(DebtRepayment.builder()
                    .debt(d1)
                    .amount(BigDecimal.valueOf(1500.00))
                    .paymentDate(today.minusDays(10))
                    .notes("Half paid back from TA stipend")
                    .build());

            Debt d2 = Debt.builder()
                    .user(student)
                    .type(DebtType.LENT)
                    .counterpartyName("Sam (Roommate)")
                    .counterpartyContact("+1-555-0144")
                    .initialAmount(BigDecimal.valueOf(1200.00))
                    .outstandingBalance(BigDecimal.valueOf(1200.00))
                    .dueDate(today.plusDays(10))
                    .description("Covered his share for university concert pass")
                    .status(DebtStatus.ACTIVE)
                    .build();
            debtRepository.save(d2);

            // Subscriptions
            List<Subscription> subs = List.of(
                    Subscription.builder().user(student).name("Spotify Student").amount(BigDecimal.valueOf(59.00)).billingCycle(BillingCycle.MONTHLY).category("ENTERTAINMENT").nextRenewalDate(today.plusDays(14)).isActive(true).description("Music streaming discount").build(),
                    Subscription.builder().user(student).name("GitHub Copilot Student").amount(BigDecimal.valueOf(0.00)).billingCycle(BillingCycle.ANNUAL).category("EDUCATION").nextRenewalDate(today.plusMonths(7)).isActive(true).description("Free student pack license").build(),
                    Subscription.builder().user(student).name("YouTube Premium Family").amount(BigDecimal.valueOf(79.00)).billingCycle(BillingCycle.MONTHLY).category("ENTERTAINMENT").nextRenewalDate(today.plusDays(4)).isActive(true).description("Split family plan").build(),
                    Subscription.builder().user(student).name("Coursera Plus").amount(BigDecimal.valueOf(1200.00)).billingCycle(BillingCycle.MONTHLY).category("EDUCATION").nextRenewalDate(today.plusDays(22)).isActive(true).description("Data Science specialization").build()
            );
            subscriptionRepository.saveAll(subs);

            // Challenges
            FinancialChallenge c1 = FinancialChallenge.builder()
                    .title("30-Day Zero Takeout Challenge")
                    .description("Eat in campus dining hall or cook for 30 consecutive days.")
                    .targetSavings(BigDecimal.valueOf(3500.00))
                    .durationDays(30)
                    .badgeName("Master Chef Saver")
                    .badgeIcon("Utensils")
                    .category("FOOD")
                    .build();
            c1 = financialChallengeRepository.save(c1);

            challengeParticipantRepository.save(ChallengeParticipant.builder()
                    .challenge(c1)
                    .user(student)
                    .startDate(today.minusDays(12))
                    .endDate(today.plusDays(18))
                    .currentProgress(BigDecimal.valueOf(2100.00))
                    .status("IN_PROGRESS")
                    .build());

            FinancialChallenge c2 = FinancialChallenge.builder()
                    .title("52-Week Micro-Savings Sprint")
                    .description("Deposit ₹250 weekly into your high-yield emergency vault.")
                    .targetSavings(BigDecimal.valueOf(13000.00))
                    .durationDays(365)
                    .badgeName("Discipline Master")
                    .badgeIcon("Award")
                    .category("SAVINGS")
                    .build();
            financialChallengeRepository.save(c2);

            // Expense Split
            ExpenseSplit split = ExpenseSplit.builder()
                    .creator(student)
                    .title("Apartment WiFi & Utilities")
                    .totalAmount(BigDecimal.valueOf(2400.00))
                    .splitType(SplitType.EQUAL)
                    .isSettled(false)
                    .members(new ArrayList<>())
                    .build();
            split = expenseSplitRepository.save(split);

            split.getMembers().add(ExpenseSplitMember.builder().expenseSplit(split).name("Sam").email("sam@stanford.edu").owedAmount(BigDecimal.valueOf(800.00)).paidAmount(BigDecimal.valueOf(800.00)).isPaid(true).build());
            split.getMembers().add(ExpenseSplitMember.builder().expenseSplit(split).name("Rohan").email("rohan@stanford.edu").owedAmount(BigDecimal.valueOf(800.00)).paidAmount(BigDecimal.ZERO).isPaid(false).build());
            split.getMembers().add(ExpenseSplitMember.builder().expenseSplit(split).name("Alex (Me)").email("alex@stanford.edu").owedAmount(BigDecimal.valueOf(800.00)).paidAmount(BigDecimal.valueOf(800.00)).isPaid(true).build());
            expenseSplitRepository.save(split);

            // Financial Journal
            List<FinancialJournal> journals = List.of(
                    FinancialJournal.builder().user(student).title("Skipped impulsive sneaker flash sale").entryDate(today.minusDays(3)).notes("Resisted buying sneakers on 30% sale. Transferred ₹3,000 directly into the MacBook fund instead. Felt rewarding!").sentiment(SentimentType.PROUD).category("SAVINGS_WIN").tags("discipline,goal").build(),
                    FinancialJournal.builder().user(student).title("Sunday meal prep success").entryDate(today.minusDays(7)).notes("Cooked 4 lunches with roommates. Saved at least ₹800 compared to cafe lunches.").sentiment(SentimentType.MOTIVATED).category("LIFESTYLE").tags("food,prep").build()
            );
            financialJournalRepository.saveAll(journals);

            // Notifications
            List<Notification> notes = List.of(
                    Notification.builder().user(student).title("Budget Check: Food & Groceries").message("You've spent ₹3,090 of your ₹6,500 monthly limit (47.5%). Pace is healthy!").type(NotificationType.BUDGET_ALERT).actionUrl("/budgets").isRead(false).build(),
                    Notification.builder().user(student).title("Upcoming Subscription Renewal").message("YouTube Premium (₹79) will renew in 4 days.").type(NotificationType.BILL_REMINDER).actionUrl("/subscriptions").isRead(false).build(),
                    Notification.builder().user(student).title("Goal Milestone Reached!").message("You are over 44% of the way to your Apple MacBook Air fund! Keep going!").type(NotificationType.GOAL_MILESTONE).actionUrl("/savings").isRead(true).build()
            );
            notificationRepository.saveAll(notes);
        }

        log.info("FINOVA default seed data initialized successfully.");
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, UserProfileRepository userProfileRepository, ExpenseCategoryRepository expenseCategoryRepository, IncomeRepository incomeRepository, ExpenseRepository expenseRepository, BudgetRepository budgetRepository, BudgetCategoryRepository budgetCategoryRepository, SavingsGoalRepository savingsGoalRepository, SavingsContributionRepository savingsContributionRepository, DebtRepository debtRepository, DebtRepaymentRepository debtRepaymentRepository, SubscriptionRepository subscriptionRepository, FinancialChallengeRepository financialChallengeRepository, ChallengeParticipantRepository challengeParticipantRepository, ExpenseSplitRepository expenseSplitRepository, FinancialJournalRepository financialJournalRepository, NotificationRepository notificationRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.budgetCategoryRepository = budgetCategoryRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.savingsContributionRepository = savingsContributionRepository;
        this.debtRepository = debtRepository;
        this.debtRepaymentRepository = debtRepaymentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.financialChallengeRepository = financialChallengeRepository;
        this.challengeParticipantRepository = challengeParticipantRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.financialJournalRepository = financialJournalRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

}
