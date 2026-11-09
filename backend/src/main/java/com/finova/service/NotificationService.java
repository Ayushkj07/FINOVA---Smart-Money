package com.finova.service;

import com.finova.entity.Notification;
import com.finova.entity.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotifications(Long userId);
    List<Notification> getUnreadNotifications(Long userId);
    long getUnreadCount(Long userId);
    void markAsRead(Long userId, Long notificationId);
    void markAllAsRead(Long userId);
    void createNotification(Long userId, String title, String message, NotificationType type, String actionUrl);
}
