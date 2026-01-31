-- Add penalty configuration to Chama table
ALTER TABLE chama ADD COLUMN late_penalty_type VARCHAR(20) DEFAULT 'NONE';
-- Options: 'NONE', 'FIXED', 'PERCENTAGE'

ALTER TABLE chama ADD COLUMN late_penalty_amount DECIMAL(15, 2) DEFAULT 0;
-- If FIXED: the penalty amount (e.g., 100.00)
-- If PERCENTAGE: the percentage as decimal (e.g., 0.05 for 5%)

-- Add field to track if penalty has been created for an invoice
ALTER TABLE invoices ADD COLUMN penalty_invoice_id BIGINT NULL REFERENCES invoices(id) ON DELETE SET NULL;
-- Links contribution invoice to its penalty invoice (prevents duplicates)

CREATE INDEX idx_invoices_penalty ON invoices(penalty_invoice_id);