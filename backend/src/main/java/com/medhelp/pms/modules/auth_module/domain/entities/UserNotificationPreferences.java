package com.medhelp.pms.modules.auth_module.domain.entities;

import com.medhelp.pms.shared.domain.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_notification_preferences", schema = "user_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationPreferences extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // General notification toggles
    @Column(name = "email_notifications_enabled", nullable = false)
    @Builder.Default
    private Boolean emailNotificationsEnabled = true;

    @Column(name = "sms_notifications_enabled", nullable = false)
    @Builder.Default
    private Boolean smsNotificationsEnabled = true;

    @Column(name = "push_notifications_enabled", nullable = false)
    @Builder.Default
    private Boolean pushNotificationsEnabled = true;

    // Prescription notifications
    @Column(name = "prescription_ready_email", nullable = false)
    @Builder.Default
    private Boolean prescriptionReadyEmail = true;

    @Column(name = "prescription_ready_sms", nullable = false)
    @Builder.Default
    private Boolean prescriptionReadySms = true;

    @Column(name = "prescription_ready_push", nullable = false)
    @Builder.Default
    private Boolean prescriptionReadyPush = true;

    // Refill reminders
    @Column(name = "refill_reminder_email", nullable = false)
    @Builder.Default
    private Boolean refillReminderEmail = true;

    @Column(name = "refill_reminder_sms", nullable = false)
    @Builder.Default
    private Boolean refillReminderSms = false;

    @Column(name = "refill_reminder_push", nullable = false)
    @Builder.Default
    private Boolean refillReminderPush = true;

    @Column(name = "refill_reminder_days_before", nullable = false)
    @Builder.Default
    private Integer refillReminderDaysBefore = 7;

    // Marketing and promotional
    @Column(name = "marketing_emails_enabled", nullable = false)
    @Builder.Default
    private Boolean marketingEmailsEnabled = false;

    @Column(name = "promotional_sms_enabled", nullable = false)
    @Builder.Default
    private Boolean promotionalSmsEnabled = false;

    // System notifications
    @Column(name = "system_alerts_email", nullable = false)
    @Builder.Default
    private Boolean systemAlertsEmail = true;

    @Column(name = "system_alerts_push", nullable = false)
    @Builder.Default
    private Boolean systemAlertsPush = true;
}
