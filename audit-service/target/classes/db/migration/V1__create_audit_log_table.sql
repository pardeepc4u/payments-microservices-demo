CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    correlation_id VARCHAR(255),
    user_id VARCHAR(255),
    entity_id VARCHAR(255),
    entity_type VARCHAR(100),
    payload TEXT,
    metadata TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    action VARCHAR(100),
    status VARCHAR(20),
    error_message TEXT,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_audit_correlation_id ON audit_logs(correlation_id);
CREATE INDEX idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);
