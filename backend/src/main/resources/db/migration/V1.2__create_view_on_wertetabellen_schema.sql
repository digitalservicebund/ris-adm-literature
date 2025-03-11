CREATE OR REPLACE
view document_types_view
AS
SELECT id, abbreviation, name
FROM lookup_tables.document_types;
