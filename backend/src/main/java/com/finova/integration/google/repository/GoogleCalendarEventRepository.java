package com.finova.integration.google.repository;

import com.finova.entity.User;
import com.finova.integration.google.entity.GoogleCalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoogleCalendarEventRepository extends JpaRepository<GoogleCalendarEvent, Long> {
    List<GoogleCalendarEvent> findByUser(User user);
    List<GoogleCalendarEvent> findByUserId(Long userId);
    Optional<GoogleCalendarEvent> findByUserAndFinancialRecordIdAndEventType(User user, Long financialRecordId, String eventType);
    Optional<GoogleCalendarEvent> findByUserIdAndFinancialRecordIdAndEventType(Long userId, Long financialRecordId, String eventType);
    Optional<GoogleCalendarEvent> findByGoogleEventId(String googleEventId);
    void deleteByUser(User user);
    void deleteByUserAndFinancialRecordIdAndEventType(User user, Long financialRecordId, String eventType);
}
