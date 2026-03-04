CREATE TABLE user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    channel_order VARCHAR(255) NOT NULL,
    timezone VARCHAR(100) NOT NULL,
    quiet_hours_enabled BOOLEAN NOT NULL DEFAULT false,
    quiet_hours_start INTEGER,
    quiet_hours_end INTEGER,
    push_token VARCHAR(500),
    phone_number VARCHAR(50),
    email VARCHAR(255),
    whatsapp_number VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX idx_user_preferences_user_id ON user_preferences(user_id);
