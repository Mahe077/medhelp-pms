package com.medhelp.pms.modules.auth_module.application.mappers;

import com.medhelp.pms.modules.auth_module.application.dtos.NotificationPreferencesDto;
import com.medhelp.pms.modules.auth_module.application.dtos.ProfileSettingsDto;
import com.medhelp.pms.modules.auth_module.application.dtos.UserPreferencesDto;
import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.entities.UserNotificationPreferences;
import org.springframework.stereotype.Component;

@Component
public class SettingsMapper {

    public ProfileSettingsDto toProfileSettingsDto(User user) {
        if (user == null) {
            return null;
        }
        return ProfileSettingsDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .licenseNumber(user.getLicenseNumber())
                .role(user.getRole())
                .userType(user.getUserType() != null ? user.getUserType().name() : null)
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .build();
    }

    public void updateUserFromProfileDto(ProfileSettingsDto dto, User user) {
        if (dto == null || user == null) {
            return;
        }
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setLicenseNumber(dto.getLicenseNumber());
    }

    public NotificationPreferencesDto toNotificationPreferencesDto(UserNotificationPreferences prefs) {
        if (prefs == null) {
            return null;
        }
        return NotificationPreferencesDto.builder()
                .id(prefs.getId())
                .userId(prefs.getUserId())
                .emailNotificationsEnabled(prefs.getEmailNotificationsEnabled())
                .smsNotificationsEnabled(prefs.getSmsNotificationsEnabled())
                .pushNotificationsEnabled(prefs.getPushNotificationsEnabled())
                .prescriptionReadyEmail(prefs.getPrescriptionReadyEmail())
                .prescriptionReadySms(prefs.getPrescriptionReadySms())
                .prescriptionReadyPush(prefs.getPrescriptionReadyPush())
                .refillReminderEmail(prefs.getRefillReminderEmail())
                .refillReminderSms(prefs.getRefillReminderSms())
                .refillReminderPush(prefs.getRefillReminderPush())
                .refillReminderDaysBefore(prefs.getRefillReminderDaysBefore())
                .marketingEmailsEnabled(prefs.getMarketingEmailsEnabled())
                .promotionalSmsEnabled(prefs.getPromotionalSmsEnabled())
                .systemAlertsEmail(prefs.getSystemAlertsEmail())
                .systemAlertsPush(prefs.getSystemAlertsPush())
                .build();
    }

    public void updatePreferencesFromDto(NotificationPreferencesDto dto, UserNotificationPreferences prefs) {
        if (dto == null || prefs == null) {
            return;
        }
        prefs.setEmailNotificationsEnabled(dto.getEmailNotificationsEnabled());
        prefs.setSmsNotificationsEnabled(dto.getSmsNotificationsEnabled());
        prefs.setPushNotificationsEnabled(dto.getPushNotificationsEnabled());
        prefs.setPrescriptionReadyEmail(dto.getPrescriptionReadyEmail());
        prefs.setPrescriptionReadySms(dto.getPrescriptionReadySms());
        prefs.setPrescriptionReadyPush(dto.getPrescriptionReadyPush());
        prefs.setRefillReminderEmail(dto.getRefillReminderEmail());
        prefs.setRefillReminderSms(dto.getRefillReminderSms());
        prefs.setRefillReminderPush(dto.getRefillReminderPush());
        prefs.setRefillReminderDaysBefore(dto.getRefillReminderDaysBefore());
        prefs.setMarketingEmailsEnabled(dto.getMarketingEmailsEnabled());
        prefs.setPromotionalSmsEnabled(dto.getPromotionalSmsEnabled());
        prefs.setSystemAlertsEmail(dto.getSystemAlertsEmail());
        prefs.setSystemAlertsPush(dto.getSystemAlertsPush());
    }

    public UserPreferencesDto toUserPreferencesDto(User user) {
        if (user == null) {
            return null;
        }
        return UserPreferencesDto.builder()
                .preferredLanguage(user.getPreferredLanguage())
                .preferredTheme(user.getPreferredTheme())
                .build();
    }

    public void updateUserPreferencesFromDto(UserPreferencesDto dto, User user) {
        if (dto == null || user == null) {
            return;
        }
        if (dto.getPreferredLanguage() != null) {
            user.setPreferredLanguage(dto.getPreferredLanguage());
        }
        if (dto.getPreferredTheme() != null) {
            user.setPreferredTheme(dto.getPreferredTheme());
        }
    }
}
