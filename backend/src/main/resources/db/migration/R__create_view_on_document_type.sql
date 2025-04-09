-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.
CREATE OR REPLACE
VIEW document_type_view
AS
SELECT id, abbreviation, label as name
FROM lookup_tables.document_type WHERE category = 'N';
