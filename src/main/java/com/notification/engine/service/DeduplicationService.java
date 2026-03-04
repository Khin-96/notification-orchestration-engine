package com.notification.engine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeduplicationService {
    
    private final StringRedisTemplate redisTemplate;
    
    @Value("${notification.deduplication.window-seconds}")
    private int windowSeconds;
    
    public boolean isDuplicate(String userId, String messageHash) {
        String key = String.format("notif:dedup:%s:%s", userId, messageHash);
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(windowSeconds));
        
        if (Boolean.FALSE.equals(isNew)) {
            log.warn("Duplicate notification detected for user: {} with hash: {}", userId, messageHash);
            return true;
        }
        
        return false;
    }
    
    public String generateHash(String title, String message) {
        return String.valueOf((title + message).hashCode());
    }
}
