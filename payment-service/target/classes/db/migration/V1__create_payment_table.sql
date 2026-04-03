CREATE TABLE IF NOT EXISTS payments (
    id VARCHAR(36) PRIMARY KEY,
    payment_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    from_account_id VARCHAR(36) NOT NULL,
    to_account_id VARCHAR(36) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    routing_number VARCHAR(9),
    account_number VARCHAR(17),
    trace_number VARCHAR(15),
    description TEXT,
    correlation_id VARCHAR(36),
    failure_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_payments_from_account ON payments(from_account_id);
CREATE INDEX IF NOT EXISTS idx_payments_to_account ON payments(to_account_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_correlation ON payments(correlation_id);
CREATE INDEX IF NOT EXISTS idx_payments_created_at ON payments(created_at);
CREATE INDEX IF NOT EXISTS idx_payments_trace_number ON payments(trace_number);
