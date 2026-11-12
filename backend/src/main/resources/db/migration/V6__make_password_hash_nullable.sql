-- =====================================================================
-- FINOVA Flyway Migration V6: Make password_hash nullable for OAuth users
-- =====================================================================

-- Allow NULL values for password_hash so Google OAuth users do not require a local password
ALTER TABLE users MODIFY COLUMN password_hash VARCHAR(255) NULL;
