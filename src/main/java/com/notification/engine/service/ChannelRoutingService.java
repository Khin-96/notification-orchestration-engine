package com.notification.engine.service;

import com.notification.engine.model.NotificationChannel;
import com.notification.engine.model.UserPreference;
import com.notification.engine.provider.ChannelProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelRoutingService {
    
    private final List<ChannelProvider> channelProviders;
    
    private Map<NotificationChannel, ChannelProvider> providerMap;
    
    private Map<NotificationChannel, ChannelProvider> getProviderMap() {
        if (providerMap == null) {
            providerMap = channelProviders.stream()
                    .collect(Collectors.toMap(ChannelProvider::getChannel, Function.identity()));
        }
        return providerMap;
    }
    
    public boolean sendWithFallback(UserPreference preference, String title, String message) {
        List<NotificationChannel> channels = preference.getChannelOrderList();
        
        for (NotificationChannel channel : channels) {
            String recipient = getRecipientForChannel(preference, channel);
            
            if (recipient == null) {
                log.warn("No recipient configured for channel {} for user {}", channel, preference.getUserId());
                continue;
            }
            
            ChannelProvider provider = getProviderMap().get(channel);
            if (provider == null) {
                log.error("No provider found for channel: {}", channel);
                continue;
            }
            
            log.info("Attempting delivery via {} for user {}", channel, preference.getUserId());
            boolean success = provider.send(recipient, title, message);
            
            if (success) {
                log.info("Successfully delivered via {} for user {}", channel, preference.getUserId());
                return true;
            } else {
                log.warn("Failed to deliver via {} for user {}, trying next channel", channel, preference.getUserId());
            }
        }
        
        log.error("All delivery channels failed for user {}", preference.getUserId());
        return false;
    }
    
    private String getRecipientForChannel(UserPreference preference, NotificationChannel channel) {
        return switch (channel) {
            case PUSH -> preference.getPushToken();
            case SMS -> preference.getPhoneNumber();
            case EMAIL -> preference.getEmail();
            case WHATSAPP -> preference.getWhatsappNumber();
        };
    }
}
