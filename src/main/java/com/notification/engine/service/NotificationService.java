package com.notification.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.engine.dto.NotificationRequest;
import com.notification.engine.dto.NotificationResponse;
import com.notification.engine.exception.DuplicateNotificationException;
import com.notification.engine.exception.UserPreferenceNotFoundException;
import com.notification.engine.model.DeliveryStatus;
import com.notification.engine.model.Notification;
import com.notification.engine.model.UserPreference;
import com.notification.engine.repository.NotificationRepository;
import com.notification.engine.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final DeduplicationService deduplicationService;
    private final QuietHoursService quietHoursService;
    private final ChannelRoutingService channelRoutingService;
    private final WebhookDeliveryService webhookDeliveryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        // Check for duplicates
        String messageHash = deduplicationService.generateHash(request.getTitle(), request.getMessage());
        if (deduplicationService.isDuplicate(request.getUserId(), messageHash)) {
            throw new DuplicateNotificationException("Duplicate notification detected within deduplication window");
        }
        
        // Get user preferences
        UserPreference preference = userPreferenceRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserPreferenceNotFoundException("User preference not found for user: " + request.getUserId()));
        
        // Create notification record
        String notificationId = UUID.randomUUID().toString();
        Notification notification = Notification.builder()
                .notificationId(notificationId)
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .priority(request.getPriority())
                .status(DeliveryStatus.PENDING)
                .metadata(serializeMetadata(request.getMetadata()))
                .webhookUrl(request.getWebhookUrl())
                .build();
        
        notification = notificationRepository.save(notification);
        
        // Publish to Kafka for async processing
        kafkaTemplate.send("notification-events", notificationId, request);
        
        log.info("Notification {} queued for user {}", notificationId, request.getUserId());
        
        return mapToResponse(notification);
    }
    
    @Transactional
    public void processNotification(String notificationId, NotificationRequest request) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        
        UserPreference preference = userPreferenceRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserPreferenceNotFoundException("User preference not found"));
        
        // Check quiet hours
        if (quietHoursService.shouldSkipDueToQuietHours(preference, request.getPriority())) {
            notification.setStatus(DeliveryStatus.SKIPPED);
            notificationRepository.save(notification);
            webhookDeliveryService.deliverWebhook(request.getWebhookUrl(), notificationId, "SKIPPED");
            return;
        }
        
        // Attempt delivery with fallback
        boolean success = channelRoutingService.sendWithFallback(preference, request.getTitle(), request.getMessage());
        
        notification.setStatus(success ? DeliveryStatus.DELIVERED : DeliveryStatus.FAILED);
        notificationRepository.save(notification);
        
        // Deliver webhook
        webhookDeliveryService.deliverWebhook(request.getWebhookUrl(), notificationId, notification.getStatus().name());
        
        log.info("Notification {} processed with status: {}", notificationId, notification.getStatus());
    }
    
    private String serializeMetadata(Object metadata) {
        if (metadata == null) return null;
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize metadata", e);
            return null;
        }
    }
    
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .priority(notification.getPriority())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
