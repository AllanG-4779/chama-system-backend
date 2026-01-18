CREATE TABLE contribution_config
(
    id               BIGSERIAL PRIMARY KEY,
    chama_id         BIGINT         NOT NULL REFERENCES chama (id) ON DELETE CASCADE,
    period           VARCHAR(7)     NOT NULL, -- YYYY-MM format
    amount           DECIMAL(15, 2) NOT NULL,
    grace_period_end DATE           NOT NULL,
    start_date       DATE           NOT NULL,
    end_date         DATE           NOT NULL,
    frequency        VARCHAR(50)    NOT NULL, -- e.g., "MONTHLY", "QUARTERLY", "ANNUALLY"
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE (chama_id, period)
);
CREATE INDEX idx_contribution_config_chama ON contribution_config (chama_id);
CREATE INDEX idx_contribution_config_period ON contribution_config (period);


CREATE TABLE invoices
(
    id         BIGSERIAL PRIMARY KEY,
    member_id  BIGINT         NOT NULL REFERENCES member (id) ON DELETE CASCADE,
    chama_id   BIGINT         NOT NULL REFERENCES chama (id) ON DELETE CASCADE,
    amount_due DECIMAL(15, 2) NOT NULL,
    period_id  bigint         NOT NULL REFERENCES contribution_config (id), -- YYYY-MM format
    issue_date DATE           NOT NULL,
    type       VARCHAR(50)    NOT NULL,                                     -- e.g., "CONTRIBUTION", "PENALTY", "OTHER"
    due_date   DATE           NOT NULL,
    status     VARCHAR(50)    NOT NULL DEFAULT 'PENDING',                   -- PENDING, PAID, OVERDUE
    created_at TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP      NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_invoices_member ON invoices (member_id);
CREATE INDEX idx_invoices_chama ON invoices (chama_id);
CREATE INDEX idx_invoices_status ON invoices (status);

CREATE TABLE ledger_entries
(
    id            BIGSERIAL PRIMARY KEY,
    member_id     BIGINT         NOT NULL REFERENCES member (id) ON DELETE CASCADE,
    chama_id      BIGINT         NOT NULL REFERENCES chama (id) ON DELETE CASCADE,
    invoice_id    BIGINT         REFERENCES invoices (id) ON DELETE SET NULL,
    entry_date    DATE           NOT NULL,
    description   TEXT,
    debit_amount  DECIMAL(15, 2) NOT NULL DEFAULT 0,
    credit_amount DECIMAL(15, 2) NOT NULL DEFAULT 0,
    balance_after DECIMAL(15, 2) NOT NULL,
    created_at    TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ledger_entries_member ON ledger_entries (member_id);
CREATE INDEX idx_ledger_entries_chama ON ledger_entries (chama_id);
CREATE INDEX idx_ledger_entries_invoice ON ledger_entries (invoice_id);
CREATE INDEX idx_ledger_entries_date ON ledger_entries (entry_date);
CREATE INDEX idx_ledger_entries_member_date ON ledger_entries (member_id, entry_date);