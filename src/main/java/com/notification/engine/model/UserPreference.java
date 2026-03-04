package com.notification.engine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String userId;
    
    @Column(name = "channel_order", nullable = false)
    private String channelOrder;
    
    @Column(name = "timezone", nullable = false)
    private String timezone;
    
    @Column(name = "quiet_hours_enabled", nullable = false)
    private Boolean quietHoursEnabled;
    
    @Column(name = "quiet_hours_start")
    private Integer quietHoursStart;
    
    @Column(name = "quiet_hours_end")
    private Integer quietHoursEnd;
    
    @Column(name = "push_token")
    private String pushToken;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "whatsapp_number")
    private String whatsappNumber;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public List<NotificationChannel> getChannelOrderList() {
        return List.of(channelOrder.split(","))
                .stream()
                .map(NotificationChannel::valueOf)
                .toList();
    }
}
