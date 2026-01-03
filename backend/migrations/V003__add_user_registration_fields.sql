-- Add new fields to users table for registration and verification
ALTER TABLE user_schema.users ADD COLUMN user_type VARCHAR(20) NOT NULL DEFAULT 'INTERNAL';
ALTER TABLE user_schema.users ADD COLUMN is_email_verified BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE user_schema.users ADD COLUMN verification_token VARCHAR(100);

-- Update existing users to be INTERNAL and verified (assuming existing users are admin-created)
UPDATE user_schema.users SET user_type = 'INTERNAL', is_email_verified = TRUE;

-- Add comment for documentation
COMMENT ON COLUMN user_schema.users.user_type IS 'INTERNAL or EXTERNAL';
COMMENT ON COLUMN user_schema.users.is_email_verified IS 'Whether the users email has been verified';
COMMENT ON COLUMN user_schema.users.verification_token IS 'Token used for email verification';
