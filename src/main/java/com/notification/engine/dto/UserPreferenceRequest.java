package com.notification.engine.dto;

import com.notification.engine.model.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Channel order is required")
    private List<NotificationChannel> channelOrder;
    
    @NotBlank(message = "Timezone is required")
    private String timezone;
    
    private Boolean quietHoursEnabled;
    private Integer quietHoursStart;
    private Integer quietHoursEnd;
    
    private String pushToken;
    private String phoneNumber;
    private String email;
    private String whatsappNumber;
}
