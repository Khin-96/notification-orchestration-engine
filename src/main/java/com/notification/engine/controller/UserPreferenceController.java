package com.notification.engine.controller;

import com.notification.engine.dto.UserPreferenceRequest;
import com.notification.engine.model.UserPreference;
import com.notification.engine.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@Tag(name = "User Preferences", description = "User notification preference management")
public class UserPreferenceController {
    
    private final UserPreferenceService userPreferenceService;
    
    @PostMapping
    @Operation(summary = "Create or update user preferences", description = "Configure notification channels and delivery preferences for a user")
    public ResponseEntity<UserPreference> createOrUpdatePreference(@Valid @RequestBody UserPreferenceRequest request) {
        UserPreference preference = userPreferenceService.createOrUpdatePreference(request);
        return ResponseEntity.ok(preference);
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user preferences", description = "Retrieve notification preferences for a specific user")
    public ResponseEntity<UserPreference> getPreference(@PathVariable String userId) {
        UserPreference preference = userPreferenceService.getPreference(userId);
        return ResponseEntity.ok(preference);
    }
}
