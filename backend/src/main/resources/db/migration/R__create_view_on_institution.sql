-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.

CREATE OR REPLACE VIEW institution_view AS
SELECT id, name, official_name, foreign_country, juris_id, type
FROM lookup_tables.institution;

CREATE OR REPLACE VIEW institution_region_view AS
SELECT institution_id, region_id
FROM lookup_tables.institution_region;
