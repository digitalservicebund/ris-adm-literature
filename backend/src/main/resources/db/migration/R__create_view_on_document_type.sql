-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.
DROP VIEW IF EXISTS document_type_view;

CREATE OR REPLACE VIEW document_type_view
AS
SELECT id,
       abbreviation,
       label   as name,
       case
           when (category = 'N') then 'VERWALTUNGSVORSCHRIFTEN'
           when (category = 'U') then 'LITERATUR_UNSELBSTAENDIG'
           when (category = 'S') then 'LITERATUR_SELBSTAENDIG'
           end as document_category
FROM lookup_tables.document_type;
