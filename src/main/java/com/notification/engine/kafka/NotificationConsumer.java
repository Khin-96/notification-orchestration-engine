package com.notification.engine.kafka;

import com.notification.engine.dto.NotificationRequest;
import com.notification.engine.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "notification-events", groupId = "notification-engine")
    public void consumeNotification(
            @Header(KafkaHeaders.RECEIVED_KEY) String notificationId,
            @Payload NotificationRequest request) {
        
        log.info("Received notification event: {} for user: {}", notificationId, request.getUserId());
        
        try {
            notificationService.processNotification(notificationId, request);
        } catch (Exception e) {
            log.error("Failed to process notification: {}", notificationId, e);
            // In production: implement DLQ (Dead Letter Queue) for failed messages
        }
    }
}
