package com.finova.scheduler;

import com.finova.entity.Subscription;
import com.finova.entity.enums.NotificationType;
import com.finova.repository.SubscriptionRepository;
import com.finova.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class FinancialScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    // Daily at 08:00 AM UTC
    @Scheduled(cron = "0 0 8 * * *")
    public void checkUpcomingSubscriptionRenewals() {
        log.info("Running daily subscription renewal check...");
        LocalDate upcomingThreshold = LocalDate.now().plusDays(3);
        List<Subscription> renewals = subscriptionRepository.findByIsActiveTrueAndNextRenewalDateLessThanEqual(upcomingThreshold);

        for (Subscription sub : renewals) {
            if (sub.getNextRenewalDate().isAfter(LocalDate.now().minusDays(1))) {
                notificationService.createNotification(
                        sub.getUser().getId(),
                        "Subscription Renewal Alert: " + sub.getName(),
                        String.format("Your subscription for %s is scheduled to renew on %s for ₹%.2f.",
                                sub.getName(), sub.getNextRenewalDate(), sub.getAmount()),
                        NotificationType.BILL_REMINDER,
                        "/subscriptions"
                );
            }
        }
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FinancialScheduler.class);

    public FinancialScheduler(SubscriptionRepository subscriptionRepository, NotificationService notificationService) {
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
    }

}
