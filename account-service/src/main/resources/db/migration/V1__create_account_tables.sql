CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(36) PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_holder_name VARCHAR(100) NOT NULL,
    routing_number VARCHAR(9),
    available_balance DECIMAL(19,2) DEFAULT 0,
    current_balance DECIMAL(19,2) DEFAULT 0,
    held_amount DECIMAL(19,2) DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS balance_holds (
    id VARCHAR(36) PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    payment_id VARCHAR(36),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_accounts_number ON accounts(account_number);
CREATE INDEX IF NOT EXISTS idx_holds_account ON balance_holds(account_id);
CREATE INDEX IF NOT EXISTS idx_holds_payment ON balance_holds(payment_id);
