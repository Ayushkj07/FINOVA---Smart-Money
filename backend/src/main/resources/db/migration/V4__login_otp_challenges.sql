-- =====================================================================
-- FINOVA Flyway Migration V4: Login OTP Challenges Table
-- =====================================================================

CREATE TABLE IF NOT EXISTS login_otp_challenges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challenge_id VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    email VARCHAR(150) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    attempts INT DEFAULT 0 NOT NULL,
    verified_at TIMESTAMP NULL,
    used_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loc_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_otp_challenge_id ON login_otp_challenges(challenge_id);
CREATE INDEX IF NOT EXISTS idx_otp_user_email ON login_otp_challenges(email);
