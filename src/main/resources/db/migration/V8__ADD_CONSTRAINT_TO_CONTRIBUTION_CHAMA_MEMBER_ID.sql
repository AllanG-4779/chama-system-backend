ALTER TABLE contribution
DROP
CONSTRAINT contribution_member_id_fkey,
    ADD CONSTRAINT contribution_member_id_fkey FOREIGN KEY (member_id) REFERENCES chama_member(id) ON DELETE
CASCADE;

ALTER TABLE ledger_entries
DROP
CONSTRAINT ledger_entries_member_id_fkey,
    ADD CONSTRAINT ledger_entries_member_id_fkey FOREIGN KEY (member_id) REFERENCES chama_member(id) ON DELETE
CASCADE;