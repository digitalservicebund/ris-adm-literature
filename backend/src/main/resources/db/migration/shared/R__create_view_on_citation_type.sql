-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.
DROP VIEW IF EXISTS citation_type_view;

CREATE OR REPLACE VIEW citation_type_view AS
SELECT ct.id,
       ct.abbreviation,
       ct.label,
       case
           when (source.label = 'V') then 'VERWALTUNGSVORSCHRIFTEN'
           when (source.label in ('U', 'S', 'L')) then 'LITERATUR'
       end as source_document_category,
       case
           when (target.label = 'V') then 'VERWALTUNGSVORSCHRIFTEN'
           when (target.label in ('U', 'S', 'L')) then 'LITERATUR'
           when (target.label = 'R') then 'RECHTSPRECHUNG'
       end as target_document_category
FROM lookup_tables.citation_type ct
         JOIN lookup_tables.document_category source ON ct.documentation_unit_document_category_id = source.id
         JOIN lookup_tables.document_category target ON ct.citation_document_category_id = target.id
