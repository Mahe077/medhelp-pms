package com.medhelp.pms.modules.auth_module.domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.pms.modules.auth_module.application.dtos.*;
import com.medhelp.pms.shared.application.dtos.AuditLogDto;
import com.medhelp.pms.modules.auth_module.application.mappers.SettingsMapper;
import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.entities.UserNotificationPreferences;
import com.medhelp.pms.modules.auth_module.domain.events.SettingsChangedEvent;
import com.medhelp.pms.modules.auth_module.domain.repositories.AuthRepository;
import com.medhelp.pms.modules.auth_module.domain.repositories.SettingsAuditRepository;
import com.medhelp.pms.modules.auth_module.domain.repositories.UserNotificationPreferencesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SettingsService {

    private final AuthRepository authRepository;
    private final UserNotificationPreferencesRepository notificationPreferencesRepository;
    private final SettingsAuditRepository settingsAuditRepository;
    private final SettingsMapper settingsMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Profile Settings
    public ProfileSettingsDto getProfileSettings(UUID userId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return settingsMapper.toProfileSettingsDto(user);
    }

    public ProfileSettingsDto updateProfileSettings(UUID userId, ProfileSettingsDto dto) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String oldValues = String.format("Name: %s %s, Phone: %s",
                user.getFirstName(), user.getLastName(), user.getPhone());

        settingsMapper.updateUserFromProfileDto(dto, user);
        User savedUser = authRepository.save(user);

        String newValues = String.format("Name: %s %s, Phone: %s",
                savedUser.getFirstName(), savedUser.getLastName(), savedUser.getPhone());

        publishSettingsChangedEvent(userId, "Profile", "ProfileInformation", oldValues, newValues, userId);

        return settingsMapper.toProfileSettingsDto(savedUser);
    }

    // Password Change
    public void changePassword(UUID userId, PasswordChangeRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = authRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        authRepository.save(user);

        publishSettingsChangedEvent(userId, "Security", "Password", "***", "***", userId);
    }

    // Notification Preferences
    public NotificationPreferencesDto getNotificationPreferences(UUID userId) {
        UserNotificationPreferences prefs = notificationPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultNotificationPreferences(userId));
        return settingsMapper.toNotificationPreferencesDto(prefs);
    }

    public NotificationPreferencesDto updateNotificationPreferences(UUID userId, NotificationPreferencesDto dto) {
        UserNotificationPreferences prefs = notificationPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultNotificationPreferences(userId));

        String oldValues = serializePreferences(prefs);
        settingsMapper.updatePreferencesFromDto(dto, prefs);
        UserNotificationPreferences savedPrefs = notificationPreferencesRepository.save(prefs);
        String newValues = serializePreferences(savedPrefs);

        publishSettingsChangedEvent(userId, "Notifications", "NotificationPreferences", oldValues, newValues, userId);

        return settingsMapper.toNotificationPreferencesDto(savedPrefs);
    }

    // User Preferences (Language, Theme)
    public UserPreferencesDto getUserPreferences(UUID userId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return settingsMapper.toUserPreferencesDto(user);
    }

    public UserPreferencesDto updateUserPreferences(UUID userId, UserPreferencesDto dto) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String oldValues = String.format("Language: %s, Theme: %s",
                user.getPreferredLanguage(), user.getPreferredTheme());

        settingsMapper.updateUserPreferencesFromDto(dto, user);
        User savedUser = authRepository.save(user);

        String newValues = String.format("Language: %s, Theme: %s",
                savedUser.getPreferredLanguage(), savedUser.getPreferredTheme());

        publishSettingsChangedEvent(userId, "Preferences", "UserPreferences", oldValues, newValues, userId);

        return settingsMapper.toUserPreferencesDto(savedUser);
    }

    // Audit Trail
    public Page<AuditLogDto> getAuditTrail(UUID userId, Pageable pageable) {
        return settingsAuditRepository.findUserSettingsAuditTrail(userId, pageable);
    }

    // Helper Methods
    private UserNotificationPreferences createDefaultNotificationPreferences(UUID userId) {
        return UserNotificationPreferences.builder()
                .userId(userId)
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(true)
                .pushNotificationsEnabled(true)
                .prescriptionReadyEmail(true)
                .prescriptionReadySms(true)
                .prescriptionReadyPush(true)
                .refillReminderEmail(true)
                .refillReminderSms(false)
                .refillReminderPush(true)
                .refillReminderDaysBefore(7)
                .marketingEmailsEnabled(false)
                .promotionalSmsEnabled(false)
                .systemAlertsEmail(true)
                .systemAlertsPush(true)
                .build();
    }

    private void publishSettingsChangedEvent(UUID userId, String category, String settingName,
            String oldValue, String newValue, UUID changedBy) {
        SettingsChangedEvent event = SettingsChangedEvent.builder()
                .userId(userId)
                .settingCategory(category)
                .settingName(settingName)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedAt(LocalDateTime.now())
                .changedBy(changedBy)
                .build();

        log.info("Settings changed: {} - {} for user {}", category, settingName, userId);
        // Event will be persisted via DomainEventEntity if event publishing is
        // configured
    }

    private String serializePreferences(UserNotificationPreferences prefs) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("email", prefs.getEmailNotificationsEnabled());
            map.put("sms", prefs.getSmsNotificationsEnabled());
            map.put("push", prefs.getPushNotificationsEnabled());
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
}
