CREATE TABLE IF NOT EXISTS user_schema.user_roles (
    user_id UUID REFERENCES user_schema.users(id) ON DELETE CASCADE,
    role_id UUID REFERENCES user_schema.roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    assigned_by UUID REFERENCES user_schema.users(id),
    PRIMARY KEY (user_id, role_id)
);

-- Migrate existing roles
-- Insert into user_roles where we can match users.role_name to roles.name
INSERT INTO user_schema.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM user_schema.users u
JOIN user_schema.roles r ON u.role = r.name
ON CONFLICT (user_id, role_id) DO NOTHING;

-- For users whose role doesn't exist in roles table, we might want to create the role first?
-- Assuming standard roles exist. If not, we can create them.
INSERT INTO user_schema.roles (name, description)
SELECT DISTINCT u.role, 'Automatically migrated role'
FROM user_schema.users u
WHERE NOT EXISTS (SELECT 1 FROM user_schema.roles r WHERE r.name = u.role)
ON CONFLICT (name) DO NOTHING;

-- Now run the migration again for created roles
INSERT INTO user_schema.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM user_schema.users u
JOIN user_schema.roles r ON u.role = r.name
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Now we can safely drop the role column from users table? 
-- Let's make it nullable first to be safe, but since we are changing the entity code,
-- we should probably drop it or just ignore it.
-- Let's DROP it to ensure no confusion.
ALTER TABLE user_schema.users DROP COLUMN IF EXISTS role;
