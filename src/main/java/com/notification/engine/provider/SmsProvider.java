package com.notification.engine.provider;

import com.notification.engine.model.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsProvider implements ChannelProvider {
    
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }
    
    @Override
    public boolean send(String phoneNumber, String title, String message) {
        log.info("Sending SMS to: {} - Message: {}", phoneNumber, message);
        
        // Simulate SMS sending
        // In production: integrate with Twilio, Africa's Talking, or AWS SNS
        try {
            Thread.sleep(150);
            return Math.random() > 0.15; // 85% success rate
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
