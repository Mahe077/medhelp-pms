-- Add user preference columns
ALTER TABLE users ADD COLUMN preferred_language VARCHAR(10) DEFAULT 'en' NOT NULL;
ALTER TABLE users ADD COLUMN preferred_theme VARCHAR(20) DEFAULT 'system' NOT NULL;
