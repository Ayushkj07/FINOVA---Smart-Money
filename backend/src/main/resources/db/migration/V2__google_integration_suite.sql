-- =====================================================================
-- FINOVA Flyway Migration V2: Google Integration Suite Tables
-- =====================================================================

-- 25. Google Accounts
CREATE TABLE IF NOT EXISTS google_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    google_user_id VARCHAR(100) NOT NULL UNIQUE,
    google_email VARCHAR(150) NOT NULL,
    profile_picture VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ga_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 26. Google Integrations
CREATE TABLE IF NOT EXISTS google_integrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    connection_status VARCHAR(30) DEFAULT 'DISCONNECTED',
    scopes VARCHAR(1000),
    last_sync_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gi_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_google_service UNIQUE (user_id, service_name)
);

-- 27. Google OAuth Tokens (AES-256-GCM Encrypted at Rest)
CREATE TABLE IF NOT EXISTS google_oauth_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    encrypted_access_token VARCHAR(2000) NOT NULL,
    encrypted_refresh_token VARCHAR(2000),
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_got_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 28. Google Calendar Events
CREATE TABLE IF NOT EXISTS google_calendar_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    financial_record_id BIGINT NOT NULL,
    google_event_id VARCHAR(150) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    sync_status VARCHAR(30) DEFAULT 'SYNCED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gce_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_record_type UNIQUE (user_id, financial_record_id, event_type)
);

-- 29. Google Drive Files
CREATE TABLE IF NOT EXISTS google_drive_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    google_file_id VARCHAR(150) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    folder_path VARCHAR(255),
    file_size BIGINT,
    web_link VARCHAR(500),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gdf_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 30. Google Sync Logs
CREATE TABLE IF NOT EXISTS google_sync_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    sync_status VARCHAR(30) NOT NULL,
    error_message VARCHAR(1000),
    details VARCHAR(2000),
    synced_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gsl_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
