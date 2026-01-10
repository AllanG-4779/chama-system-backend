CREATE TABLE chama
(
    id                    BIGSERIAL PRIMARY KEY,
    name                  VARCHAR(255)        NOT NULL,
    registration_number   VARCHAR(100) UNIQUE NOT NULL,
    contribution_amount   DECIMAL(15, 2)      NOT NULL,
    description           TEXT,
    contribution_schedule VARCHAR(255),
    status                VARCHAR(50)         NOT NULL DEFAULT 'ACTIVE',
    created_at            TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP           NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_chama_status ON chama (status);

CREATE TABLE member
(
    id            BIGSERIAL PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    date_of_birth DATE         NOT NULL,
    phone_number  VARCHAR(20)  NOT NULL,
    email         VARCHAR(255),
    id_number     VARCHAR(50)  NOT NULL,
    status        VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE chama_member
(
    id         BIGSERIAL PRIMARY KEY,
    chama_id   BIGINT      NOT NULL REFERENCES chama (id) ON DELETE CASCADE,
    member_id  BIGINT      NOT NULL REFERENCES member (id) ON DELETE CASCADE,
    joined_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    role       VARCHAR(50) NOT NULL DEFAULT 'MEMBER', -- MEMBER, CHAIRMAN, TREASURER, SECRETARY,
    status     VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP   NOT NULL DEFAULT now(),
    UNIQUE (chama_id, member_id)

);

CREATE INDEX idx_member_phone ON member (phone_number);
CREATE INDEX idx_member_status ON member (status);


CREATE TABLE contribution
(
    id                         BIGSERIAL PRIMARY KEY,
    member_id                  BIGINT         NOT NULL REFERENCES member (id) ON DELETE CASCADE,
    chama_id                   BIGINT         NOT NULL REFERENCES chama (id) ON DELETE CASCADE,
    amount                     DECIMAL(15, 2) NOT NULL,
    contribution_date          DATE           NOT NULL,
    contribution_period        VARCHAR(7)     NOT NULL, -- YYYY-MM format
    payment_method             VARCHAR(50)    NOT NULL,
    payment_reference          VARCHAR(100),
    external_payment_reference VARCHAR(100),
    recorded_by                VARCHAR(255)   NOT NULL,
    recorded_at                TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contribution_member ON contribution (member_id);
CREATE INDEX idx_contribution_chama ON contribution (chama_id);
CREATE INDEX idx_contribution_period ON contribution (contribution_period);
CREATE INDEX idx_contribution_date ON contribution (contribution_date);



CREATE TABLE loan
(
    id                       BIGSERIAL PRIMARY KEY,
    member_id                BIGINT         NOT NULL REFERENCES member (id) ON DELETE CASCADE,
    chama_id                 BIGINT         NOT NULL REFERENCES chama (id) ON DELETE CASCADE,
    principal_amount         DECIMAL(15, 2) NOT NULL,
    interest_rate            DECIMAL(5, 2)  NOT NULL,
    repayment_months         INTEGER        NOT NULL,
    total_amount             DECIMAL(15, 2) NOT NULL,
    monthly_installment      DECIMAL(15, 2) NOT NULL,
    application_date         DATE           NOT NULL,
    approval_date            DATE,
    disbursement_date        DATE,
    expected_completion_date DATE,
    status                   VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    approved_by              VARCHAR(255),
    rejection_reason         TEXT,
    created_at               TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_loan_member ON loan (member_id);
CREATE INDEX idx_loan_chama ON loan (chama_id);
CREATE INDEX idx_loan_status ON loan (status);
CREATE INDEX idx_loan_application_date ON loan (application_date);


CREATE TABLE loan_repayment
(
    id                         BIGSERIAL PRIMARY KEY,
    loan_id                    BIGINT         NOT NULL REFERENCES loan (id) ON DELETE CASCADE,
    amount                     DECIMAL(15, 2) NOT NULL,
    payment_date               DATE           NOT NULL,
    repayment_period           VARCHAR(7)     NOT NULL, -- YYYY-MM format
    payment_method             VARCHAR(50)    NOT NULL,
    payment_reference          VARCHAR(100),
    external_payment_reference VARCHAR(100),
    recorded_by                VARCHAR(255)   NOT NULL,
    recorded_at                TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_loan_repayment_loan ON loan_repayment (loan_id);
CREATE INDEX idx_loan_repayment_date ON loan_repayment (payment_date);
CREATE INDEX idx_loan_repayment_period ON loan_repayment (repayment_period);



CREATE TABLE penalty
(
    id            BIGSERIAL PRIMARY KEY,
    member_id     BIGINT         NOT NULL REFERENCES member (id) ON DELETE CASCADE,
    chama_id      BIGINT         NOT NULL REFERENCES chama (id) ON DELETE CASCADE,
    type          VARCHAR(50)    NOT NULL,
    amount        DECIMAL(15, 2) NOT NULL,
    incurred_date DATE           NOT NULL,
    reason        TEXT,
    paid          BOOLEAN        NOT NULL DEFAULT FALSE,
    paid_date     DATE,
    recorded_by   VARCHAR(255)   NOT NULL,
    created_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_penalty_member ON penalty (member_id);
CREATE INDEX idx_penalty_chama ON penalty (chama_id);
CREATE INDEX idx_penalty_paid ON penalty (paid);
CREATE INDEX idx_penalty_incurred_date ON penalty (incurred_date);


CREATE TABLE app_user
(
    id            BIGSERIAL PRIMARY KEY,
    member_id     BIGINT UNIQUE REFERENCES member (id) ON DELETE CASCADE,
    username      VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    roles         TEXT[]              NOT NULL, -- PostgreSQL array for roles
    active        BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_username ON app_user (username);
CREATE INDEX idx_user_member ON app_user (member_id);

CREATE TABLE transaction_audit_log
(
    id                BIGSERIAL PRIMARY KEY,

    -- Transaction identification
    transaction_type  VARCHAR(50)  NOT NULL, -- CONTRIBUTION, LOAN, LOAN_REPAYMENT, PENALTY, DISBURSEMENT
    transaction_id    BIGINT       NOT NULL,
    reference_number  VARCHAR(100),          -- M-Pesa receipt, loan reference, etc.

    -- Financial details
    amount            DECIMAL(15, 2),
    currency          VARCHAR(3)   NOT NULL DEFAULT 'KES',

    -- Entities involved
    member_id         BIGINT       NOT NULL REFERENCES member (id) ON DELETE CASCADE,
    chama_id          BIGINT       NOT NULL REFERENCES chama (id) ON DELETE CASCADE,

    -- Action details
    action            VARCHAR(50)  NOT NULL, -- CREATED, APPROVED, REJECTED, DISBURSED, REVERSED
    status_before     VARCHAR(50),           -- Previous status (for updates)
    status_after      VARCHAR(50),           -- New status

    -- User who performed the action
    performed_by_id   BIGINT       REFERENCES app_user (id) ON DELETE SET NULL,
    performed_by_name VARCHAR(255) NOT NULL,
    performed_by_role VARCHAR(50)  NOT NULL, -- CHAIRMAN, TREASURER, SECRETARY

    -- Additional context
    notes             TEXT,                  -- Optional reason/comment
    metadata          JSONB,                 -- Store additional transaction data

    -- Client information
    ip_address        VARCHAR(45),
    user_agent        TEXT,

    -- Timestamp
    timestamp         TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Indexes for fast queries
CREATE INDEX idx_transaction_audit_member ON transaction_audit_log (member_id);
CREATE INDEX idx_transaction_audit_chama ON transaction_audit_log (chama_id);
CREATE INDEX idx_transaction_audit_type ON transaction_audit_log (transaction_type);
CREATE INDEX idx_transaction_audit_timestamp ON transaction_audit_log (timestamp DESC);
CREATE INDEX idx_transaction_audit_action ON transaction_audit_log (action);
CREATE INDEX idx_transaction_audit_reference ON transaction_audit_log (reference_number);
CREATE INDEX idx_transaction_audit_performed_by ON transaction_audit_log (performed_by_id);

-- Composite index for common queries
CREATE INDEX idx_transaction_audit_chama_type_timestamp
    ON transaction_audit_log (chama_id, transaction_type, timestamp DESC);