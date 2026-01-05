-- V007__fix_event_publication_table.sql
-- Fix Spring Modulith's event_publication table column sizes to handle long serialized events and listener IDs

-- Create the table if it doesn't exist yet (in case ddl-auto hasn't run or is disabled)
-- This ensures the table exists before we try to alter it
CREATE TABLE IF NOT EXISTS event_publication (
    id UUID NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    listener_id VARCHAR(255) NOT NULL,
    serialized_event VARCHAR(255) NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) NOT NULL,
    completion_attempts INTEGER DEFAULT 0,
    last_resubmission_date TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);

-- Alter columns to handle larger values
ALTER TABLE event_publication ALTER COLUMN serialized_event TYPE TEXT;
ALTER TABLE event_publication ALTER COLUMN listener_id TYPE VARCHAR(1024);
ALTER TABLE event_publication ALTER COLUMN event_type TYPE VARCHAR(1024);

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_event_publication_status ON event_publication(status);
CREATE INDEX IF NOT EXISTS idx_event_publication_event_type ON event_publication(event_type);
