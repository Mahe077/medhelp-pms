package com.medhelp.pms.modules.auth_module.api.controllers;

import com.medhelp.pms.modules.auth_module.application.dtos.*;
import com.medhelp.pms.modules.auth_module.domain.services.SettingsService;
import com.medhelp.pms.shared.application.dtos.AuditLogDto;
import com.medhelp.pms.modules.auth_module.domain.entities.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class SettingsController {

    private final SettingsService settingsService;

    // Profile Settings
    @GetMapping("/profile")
    public ResponseEntity<ProfileSettingsDto> getProfileSettings(@AuthenticationPrincipal User user) {
        log.info("Getting profile settings for user: {}", user.getId());
        ProfileSettingsDto settings = settingsService.getProfileSettings(user.getId());
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileSettingsDto> updateProfileSettings(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileSettingsDto dto) {
        log.info("Updating profile settings for user: {}", user.getId());
        ProfileSettingsDto updated = settingsService.updateProfileSettings(user.getId(), dto);
        return ResponseEntity.ok(updated);
    }

    // Security Settings
    @PutMapping("/security/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PasswordChangeRequest request) {
        log.info("Changing password for user: {}", user.getId());
        try {
            settingsService.changePassword(user.getId(), request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Notification Preferences
    @GetMapping("/notifications")
    public ResponseEntity<NotificationPreferencesDto> getNotificationPreferences(@AuthenticationPrincipal User user) {
        log.info("Getting notification preferences for user: {}", user.getId());
        NotificationPreferencesDto prefs = settingsService.getNotificationPreferences(user.getId());
        return ResponseEntity.ok(prefs);
    }

    @PutMapping("/notifications")
    public ResponseEntity<NotificationPreferencesDto> updateNotificationPreferences(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody NotificationPreferencesDto dto) {
        log.info("Updating notification preferences for user: {}", user.getId());
        NotificationPreferencesDto updated = settingsService.updateNotificationPreferences(user.getId(), dto);
        return ResponseEntity.ok(updated);
    }

    // User Preferences (Language, Theme)
    @GetMapping("/preferences")
    public ResponseEntity<UserPreferencesDto> getUserPreferences(@AuthenticationPrincipal User user) {
        log.info("Getting user preferences for user: {}", user.getId());
        UserPreferencesDto prefs = settingsService.getUserPreferences(user.getId());
        return ResponseEntity.ok(prefs);
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserPreferencesDto> updateUserPreferences(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserPreferencesDto dto) {
        log.info("Updating user preferences for user: {}", user.getId());
        UserPreferencesDto updated = settingsService.updateUserPreferences(user.getId(), dto);
        return ResponseEntity.ok(updated);
    }

    // Audit Trail
    @GetMapping("/audit-trail")
    public ResponseEntity<Page<AuditLogDto>> getAuditTrail(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Getting audit trail for user: {} (page: {}, size: {})", user.getId(), page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDto> auditTrail = settingsService.getAuditTrail(user.getId(), pageable);
        return ResponseEntity.ok(auditTrail);
    }
}
