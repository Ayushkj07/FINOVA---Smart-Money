package com.finova;

import com.finova.entity.*;
import com.finova.entity.enums.PaymentMethod;
import com.finova.entity.enums.RoleType;
import com.finova.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class GoogleUserRegistrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthAccountRepository oauthAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    @Transactional
    @DisplayName("Scenario 1 & 2: New Google user registers with NULL password_hash")
    void testGoogleUserRegistrationWithNullPassword() {
        Role studentRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_STUDENT)
                        .description("Student role")
                        .build()));

        String googleSub1 = "google-sub-" + UUID.randomUUID();
        String googleEmail1 = "googleuser1-" + System.currentTimeMillis() + "@gmail.com";

        // Save new Google-only user
        User googleUser1 = User.builder()
                .email(googleEmail1)
                .fullName("Google Test User One")
                .password(null) // NULL password for Google users
                .isActive(true)
                .isVerified(true)
                .accountStatus("ACTIVE")
                .profilePictureUrl("https://lh3.googleusercontent.com/a/test-pic-1")
                .roles(new HashSet<>(Collections.singleton(studentRole)))
                .build();
        googleUser1.setEmailVerified(true);

        User savedUser1 = userRepository.save(googleUser1);

        assertNotNull(savedUser1.getId(), "Google user must be assigned a unique ID");
        assertNull(savedUser1.getPassword(), "Google-only user password_hash must be NULL");
        assertTrue(savedUser1.getIsVerified(), "Google-only user must be marked verified");

        // Link OAuth account
        OAuthAccount oauth1 = OAuthAccount.builder()
                .user(savedUser1)
                .provider("GOOGLE")
                .providerUserId(googleSub1)
                .providerEmail(googleEmail1)
                .build();
        oauthAccountRepository.save(oauth1);

        // Verify entity update and merge works without Hibernate PersistentSet UnsupportedOperationException
        savedUser1.setLastLogin(Instant.now());
        savedUser1 = userRepository.save(savedUser1);
        assertNotNull(savedUser1.getLastLogin(), "User entity update after save must succeed without collection errors");

        // Scenario 3: Verify same Google user can log in again and find existing record
        Optional<OAuthAccount> foundOAuth = oauthAccountRepository.findByProviderAndProviderUserId("GOOGLE", googleSub1);
        assertTrue(foundOAuth.isPresent(), "Existing Google OAuth record must be found on subsequent logins");
        assertEquals(savedUser1.getId(), foundOAuth.get().getUser().getId(), "Must resolve to the exact same user ID");

        // Scenario 4: A different Google user gets a separate account
        String googleSub2 = "google-sub-" + UUID.randomUUID();
        String googleEmail2 = "googleuser2-" + System.currentTimeMillis() + "@gmail.com";

        User googleUser2 = User.builder()
                .email(googleEmail2)
                .fullName("Google Test User Two")
                .password(null)
                .isActive(true)
                .isVerified(true)
                .accountStatus("ACTIVE")
                .roles(new HashSet<>(Collections.singleton(studentRole)))
                .build();
        googleUser2.setEmailVerified(true);
        User savedUser2 = userRepository.save(googleUser2);

        OAuthAccount oauth2 = OAuthAccount.builder()
                .user(savedUser2)
                .provider("GOOGLE")
                .providerUserId(googleSub2)
                .providerEmail(googleEmail2)
                .build();
        oauthAccountRepository.save(oauth2);

        assertNotEquals(savedUser1.getId(), savedUser2.getId(), "Two different Google users must have distinct account IDs");

        // Scenario 8: Cross-user data isolation test
        Expense exp1 = Expense.builder()
                .user(savedUser1)
                .title("User 1 Textbook")
                .amount(BigDecimal.valueOf(1200))
                .categoryName("Education")
                .date(LocalDate.now())
                .paymentMethod(PaymentMethod.UPI)
                .build();
        expenseRepository.save(exp1);

        List<Expense> user1Expenses = expenseRepository.findByUserIdOrderByDateDesc(savedUser1.getId());
        List<Expense> user2Expenses = expenseRepository.findByUserIdOrderByDateDesc(savedUser2.getId());

        assertEquals(1, user1Expenses.size(), "User 1 must see their own expense");
        assertEquals(0, user2Expenses.size(), "User 2 must NOT see User 1's expenses (data isolation enforced)");
    }
}
