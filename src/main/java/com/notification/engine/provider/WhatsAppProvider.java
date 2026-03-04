package com.notification.engine.provider;

import com.notification.engine.model.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WhatsAppProvider implements ChannelProvider {
    
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.WHATSAPP;
    }
    
    @Override
    public boolean send(String whatsappNumber, String title, String message) {
        log.info("Sending WhatsApp to: {} - Message: {}", whatsappNumber, message);
        
        // Simulate WhatsApp sending
        // In production: integrate with Twilio WhatsApp API or Meta WhatsApp Business API
        try {
            Thread.sleep(180);
            return Math.random() > 0.12; // 88% success rate
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
