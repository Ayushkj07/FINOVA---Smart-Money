package com.finova.integration.google.service;

import com.finova.integration.google.dto.GoogleCalendarEventDto;
import com.finova.integration.google.dto.GoogleCalendarSyncResult;

import java.util.List;

public interface GoogleCalendarService {
    GoogleCalendarSyncResult syncFinancialEvents(Long userId);
    List<GoogleCalendarEventDto> getUpcomingSyncedEvents(Long userId);
    void deleteCalendarEvent(Long userId, Long eventRecordId);
}
