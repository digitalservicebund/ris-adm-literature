-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.
CREATE OR REPLACE
VIEW document_types_view
AS
SELECT id, abbreviation, name
FROM lookup_tables.document_types;
