-- View uses foreign schema "lookup_tables", therefore it is repeatable.
-- This migration is executed every time this script is changed.
DROP VIEW IF EXISTS verweis_typ_view;

CREATE OR REPLACE VIEW verweis_typ_view AS
SELECT vt.id,
       vt.name,
       vt.typ_nummer,
       vt.public_id
FROM lookup_tables.verweistyp vt
