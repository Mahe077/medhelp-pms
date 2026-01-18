package com.medhelp.pms.modules.auth_module.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesDto {

    private UUID id;
    private UUID userId;

    // General notification toggles
    private Boolean emailNotificationsEnabled;
    private Boolean smsNotificationsEnabled;
    private Boolean pushNotificationsEnabled;

    // Prescription notifications
    private Boolean prescriptionReadyEmail;
    private Boolean prescriptionReadySms;
    private Boolean prescriptionReadyPush;

    // Refill reminders
    private Boolean refillReminderEmail;
    private Boolean refillReminderSms;
    private Boolean refillReminderPush;
    private Integer refillReminderDaysBefore;

    // Marketing and promotional
    private Boolean marketingEmailsEnabled;
    private Boolean promotionalSmsEnabled;

    // System notifications
    private Boolean systemAlertsEmail;
    private Boolean systemAlertsPush;
}
