package com.notification.engine.dto;

import com.notification.engine.model.DeliveryStatus;
import com.notification.engine.model.NotificationPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private NotificationPriority priority;
    private DeliveryStatus status;
    private LocalDateTime createdAt;
}
