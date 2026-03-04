package com.notification.engine.service;

import com.notification.engine.dto.UserPreferenceRequest;
import com.notification.engine.model.NotificationChannel;
import com.notification.engine.model.UserPreference;
import com.notification.engine.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceService {
    
    private final UserPreferenceRepository userPreferenceRepository;
    
    @Transactional
    public UserPreference createOrUpdatePreference(UserPreferenceRequest request) {
        UserPreference preference = userPreferenceRepository.findByUserId(request.getUserId())
                .orElse(new UserPreference());
        
        preference.setUserId(request.getUserId());
        preference.setChannelOrder(request.getChannelOrder().stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")));
        preference.setTimezone(request.getTimezone());
        preference.setQuietHoursEnabled(request.getQuietHoursEnabled() != null ? request.getQuietHoursEnabled() : false);
        preference.setQuietHoursStart(request.getQuietHoursStart());
        preference.setQuietHoursEnd(request.getQuietHoursEnd());
        preference.setPushToken(request.getPushToken());
        preference.setPhoneNumber(request.getPhoneNumber());
        preference.setEmail(request.getEmail());
        preference.setWhatsappNumber(request.getWhatsappNumber());
        
        preference = userPreferenceRepository.save(preference);
        log.info("User preference saved for user: {}", request.getUserId());
        
        return preference;
    }
    
    public UserPreference getPreference(String userId) {
        return userPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User preference not found for user: " + userId));
    }
}
