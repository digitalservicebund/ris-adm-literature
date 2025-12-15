ALTER TABLE IF EXISTS
    documentation_unit_index
    RENAME COLUMN fundstellen TO fundstellen_combined;
ALTER TABLE IF EXISTS
    documentation_unit_index
    RENAME COLUMN zitierdaten TO zitierdaten_combined;
ALTER TABLE IF EXISTS
    documentation_unit_index
    ADD COLUMN IF NOT EXISTS fundstellen                text[],
    ADD COLUMN IF NOT EXISTS zitierdaten                text[],
    ADD COLUMN IF NOT EXISTS normgeber_list_combined    text,
    ADD COLUMN IF NOT EXISTS normgeber_list             text[],
    ADD COLUMN IF NOT EXISTS aktenzeichen_list_combined text,
    ADD COLUMN IF NOT EXISTS aktenzeichen_list          text[],
    ADD COLUMN IF NOT EXISTS inkrafttretedatum          text,
    ADD COLUMN IF NOT EXISTS dokumenttyp                text;
