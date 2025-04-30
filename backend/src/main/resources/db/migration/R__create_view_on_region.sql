-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.

CREATE OR REPLACE VIEW region_view AS
SELECT id, code, long_text
FROM lookup_tables.region;
