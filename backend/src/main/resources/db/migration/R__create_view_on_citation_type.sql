-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.

CREATE OR REPLACE VIEW citation_type_view AS
SELECT id, abbreviation, label
FROM lookup_tables.citation_type;
