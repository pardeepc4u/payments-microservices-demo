CREATE TABLE IF NOT EXISTS settlement_batches (
    id BIGSERIAL PRIMARY KEY,
    batch_date TIMESTAMP WITH TIME ZONE NOT NULL,
    total_payments INTEGER DEFAULT 0,
    total_amount DECIMAL(19, 4),
    status VARCHAR(20) NOT NULL,
    settlement_reference VARCHAR(100),
    bank_reference VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_settlement_batch_date ON settlement_batches(batch_date);
CREATE INDEX idx_settlement_status ON settlement_batches(status);

CREATE TABLE IF NOT EXISTS settlement_entries (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL REFERENCES settlement_batches(id),
    payment_id VARCHAR(255) NOT NULL,
    account_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) NOT NULL,
    correlation_id VARCHAR(255),
    reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    settled_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_settlement_entry_batch ON settlement_entries(batch_id);
CREATE INDEX idx_settlement_entry_payment ON settlement_entries(payment_id);
CREATE INDEX idx_settlement_entry_status ON settlement_entries(status);
