-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.

CREATE OR REPLACE VIEW legal_periodical_view AS
SELECT id, abbreviation, title, subtitle, citation_style, juris_id, public_id
FROM lookup_tables.legal_periodical;

