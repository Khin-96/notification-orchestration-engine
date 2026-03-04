package com.notification.engine.provider;

import com.notification.engine.model.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailProvider implements ChannelProvider {
    
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }
    
    @Override
    public boolean send(String email, String title, String message) {
        log.info("Sending EMAIL to: {} - Subject: {}", email, title);
        
        // Simulate email sending
        // In production: integrate with SendGrid, AWS SES, or Mailgun
        try {
            Thread.sleep(200);
            return Math.random() > 0.05; // 95% success rate
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
