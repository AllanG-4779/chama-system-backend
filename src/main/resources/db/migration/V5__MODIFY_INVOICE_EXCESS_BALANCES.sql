ALTER TABLE  invoices DROP COLUMN IF EXISTS excess_balance;
ALTER TABLE invoices DROP COLUMN IF EXISTS amount_paid;
ALTER TABLE invoices ADD COLUMN amount_paid DECIMAL(15, 2) NOT NULL DEFAULT 0;
ALTER TABLE invoices ADD COLUMN excess_balance DECIMAL(15, 2) GENERATED ALWAYS AS (GREATEST(amount_paid - amount_due, 0.0)) STORED;
ALTER TABLE invoices ADD COLUMN amount_outstanding DECIMAL(15, 2) GENERATED ALWAYS AS (GREATEST(amount_due - amount_paid, 0.0)) STORED;


