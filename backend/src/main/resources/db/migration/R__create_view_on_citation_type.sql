-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.
DROP VIEW IF EXISTS citation_type_view;

CREATE OR REPLACE VIEW citation_type_view AS
SELECT ct.id,
       ct.abbreviation,
       ct.label,
       case
           when (dc.label = 'V') then 'VERWALTUNGSVORSCHRIFTEN'
           when (dc.label in ('U', 'L')) then 'LITERATUR_UNSELBSTAENDIG'
           when (dc.label = 'S') then 'LITERATUR_SELBSTAENDIG'
       end as document_category
FROM lookup_tables.citation_type ct
         JOIN lookup_tables.document_category dc ON ct.documentation_unit_document_category_id = dc.id
