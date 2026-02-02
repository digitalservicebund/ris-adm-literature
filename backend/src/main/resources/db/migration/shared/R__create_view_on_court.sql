-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.

CREATE OR REPLACE VIEW court_view AS
SELECT id, type, location
FROM lookup_tables.court;
