ALTER TABLE IF EXISTS
    documentation_unit_index
    DROP COLUMN IF EXISTS documentation_unit_type,
    DROP COLUMN IF EXISTS documentation_office,
    ADD COLUMN IF NOT EXISTS titel                  text,
    ADD COLUMN IF NOT EXISTS veroeffentlichungsjahr text,
    ADD COLUMN IF NOT EXISTS dokumenttypen          text,
    ADD COLUMN IF NOT EXISTS verfasser              text;
