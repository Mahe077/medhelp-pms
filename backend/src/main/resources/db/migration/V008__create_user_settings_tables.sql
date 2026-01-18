-- Migration to create user-specific settings tables
-- V008__create_user_settings_tables.sql

-- User Notification Preferences Table
CREATE TABLE IF NOT EXISTS user_schema.user_notification_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES user_schema.users(id) ON DELETE CASCADE,
    
    -- General notification toggles
    email_notifications_enabled BOOLEAN DEFAULT true,
    sms_notifications_enabled BOOLEAN DEFAULT true,
    push_notifications_enabled BOOLEAN DEFAULT true,
    
    -- Prescription notifications
    prescription_ready_email BOOLEAN DEFAULT true,
    prescription_ready_sms BOOLEAN DEFAULT true,
    prescription_ready_push BOOLEAN DEFAULT true,
    
    -- Refill reminders
    refill_reminder_email BOOLEAN DEFAULT true,
    refill_reminder_sms BOOLEAN DEFAULT false,
    refill_reminder_push BOOLEAN DEFAULT true,
    refill_reminder_days_before INTEGER DEFAULT 7,
    
    -- Marketing and promotional
    marketing_emails_enabled BOOLEAN DEFAULT false,
    promotional_sms_enabled BOOLEAN DEFAULT false,
    
    -- System notifications
    system_alerts_email BOOLEAN DEFAULT true,
    system_alerts_push BOOLEAN DEFAULT true,
    
    -- Audit columns (from BaseEntity)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES user_schema.users(id),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID REFERENCES user_schema.users(id),
    deleted_at TIMESTAMP WITH TIME ZONE,
    version INTEGER DEFAULT 1
);

-- Indexes for user notification preferences
CREATE INDEX IF NOT EXISTS idx_notification_prefs_user_id ON user_schema.user_notification_preferences(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_prefs_updated_at ON user_schema.user_notification_preferences(updated_at);

-- Comments for documentation
COMMENT ON TABLE user_schema.user_notification_preferences IS 'Stores user-specific notification preferences for email, SMS, and push notifications';
COMMENT ON COLUMN user_schema.user_notification_preferences.refill_reminder_days_before IS 'Number of days before refill due date to send reminder notification';
