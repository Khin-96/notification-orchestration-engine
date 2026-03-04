package com.notification.engine.service;

import com.notification.engine.model.NotificationPriority;
import com.notification.engine.model.UserPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
public class QuietHoursService {
    
    @Value("${notification.quiet-hours.enabled}")
    private boolean globalQuietHoursEnabled;
    
    @Value("${notification.quiet-hours.start-hour}")
    private int defaultStartHour;
    
    @Value("${notification.quiet-hours.end-hour}")
    private int defaultEndHour;
    
    public boolean shouldSkipDueToQuietHours(UserPreference preference, NotificationPriority priority) {
        // HIGH and CRITICAL priority bypass quiet hours
        if (priority == NotificationPriority.HIGH || priority == NotificationPriority.CRITICAL) {
            return false;
        }
        
        if (!globalQuietHoursEnabled || !preference.getQuietHoursEnabled()) {
            return false;
        }
        
        int startHour = preference.getQuietHoursStart() != null ? preference.getQuietHoursStart() : defaultStartHour;
        int endHour = preference.getQuietHoursEnd() != null ? preference.getQuietHoursEnd() : defaultEndHour;
        
        ZonedDateTime userTime = ZonedDateTime.now(ZoneId.of(preference.getTimezone()));
        int currentHour = userTime.getHour();
        
        boolean inQuietHours;
        if (startHour < endHour) {
            inQuietHours = currentHour >= startHour && currentHour < endHour;
        } else {
            inQuietHours = currentHour >= startHour || currentHour < endHour;
        }
        
        if (inQuietHours) {
            log.info("Skipping notification for user {} due to quiet hours ({}:00-{}:00 in {})", 
                    preference.getUserId(), startHour, endHour, preference.getTimezone());
        }
        
        return inQuietHours;
    }
}
