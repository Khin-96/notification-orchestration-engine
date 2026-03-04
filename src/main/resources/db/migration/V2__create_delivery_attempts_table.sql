CREATE TABLE delivery_attempts (
    id BIGSERIAL PRIMARY KEY,
    notification_id VARCHAR(255) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    attempt_number INTEGER NOT NULL,
    error_message VARCHAR(500),
    provider_response TEXT,
    attempted_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_delivery_attempts_notification_id ON delivery_attempts(notification_id);
CREATE INDEX idx_delivery_attempts_attempted_at ON delivery_attempts(attempted_at);
