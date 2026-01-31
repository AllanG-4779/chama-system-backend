CREATE TABLE invoice_excess_applications (
    id SERIAL PRIMARY KEY ,
    source_invoice_id BIGINT NOT NULL,
    target_invoice_id BIGINT NOT NULL,
    amount_applied DECIMAL(15, 2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_source_invoice FOREIGN KEY (source_invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT fk_target_invoice FOREIGN KEY (target_invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT check_amount_positive CHECK (amount_applied > 0)
);

CREATE INDEX idx_source_invoice ON invoice_excess_applications(source_invoice_id);
CREATE INDEX idx_target_invoice ON invoice_excess_applications(target_invoice_id);