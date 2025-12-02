-- Add columns
ALTER TABLE document_number
    ADD COLUMN prefix VARCHAR(16);

ALTER TABLE documentation_unit
    ADD COLUMN documentation_unit_type VARCHAR(255);

ALTER TABLE documentation_unit
    ADD COLUMN documentation_office VARCHAR(255);

ALTER TABLE literature_documentation_unit_index
    ADD COLUMN documentation_unit_type VARCHAR(255);

ALTER TABLE literature_documentation_unit_index
    ADD COLUMN documentation_office VARCHAR(255);


-- Backfill 'document_number' table
UPDATE document_number
SET prefix = SUBSTRING(latest FROM 1 FOR 4)
WHERE prefix IS NULL;


-- ### Backfill 'documentation_unit' table ###
-- Backfill type
UPDATE documentation_unit
SET documentation_unit_type = 'VERWALTUNGSVORSCHRIFTEN'
WHERE SUBSTRING(document_number FROM 3 FOR 2) = 'NR' AND documentation_unit_type IS NULL;

UPDATE documentation_unit
SET documentation_unit_type = 'LITERATUR_UNSELBSTSTAENDIG'
WHERE SUBSTRING(document_number FROM 3 FOR 2) = 'LU' AND documentation_unit_type IS NULL;

UPDATE documentation_unit
SET documentation_unit_type = 'LITERATUR_SELBSTSTAENDIG'
WHERE SUBSTRING(document_number FROM 3 FOR 2) = 'LS' AND documentation_unit_type IS NULL;

-- Log and default unmatched types
DO $$
DECLARE
    rec RECORD;
BEGIN
    FOR rec IN SELECT id, document_number FROM documentation_unit WHERE documentation_unit_type IS NULL LOOP
            RAISE NOTICE 'Defaulting documentation_unit_type to VERWALTUNGSVORSCHRIFTEN for document_number: % (ID: %)', rec.document_number, rec.id;
        END LOOP;
    UPDATE documentation_unit SET documentation_unit_type = 'VERWALTUNGSVORSCHRIFTEN' WHERE documentation_unit_type IS NULL;
END $$;

-- Backfill office
UPDATE documentation_unit
SET documentation_office = 'BAG'
WHERE SUBSTRING(document_number FROM 1 FOR 2) = 'KA' AND documentation_office IS NULL;

UPDATE documentation_unit
SET documentation_office = 'BFH'
WHERE SUBSTRING(document_number FROM 1 FOR 2) = 'ST' AND documentation_office IS NULL;

UPDATE documentation_unit
SET documentation_office = 'BSG'
WHERE SUBSTRING(document_number FROM 1 FOR 2) = 'KS' AND documentation_office IS NULL;

UPDATE documentation_unit
SET documentation_office = 'BVERFG'
WHERE SUBSTRING(document_number FROM 1 FOR 2) = 'KV' AND documentation_office IS NULL;

UPDATE documentation_unit
SET documentation_office = 'BVERWG'
WHERE SUBSTRING(document_number FROM 1 FOR 2) = 'WB' AND documentation_office IS NULL;

-- Log and default unmatched offices
DO $$
DECLARE
    rec RECORD;
BEGIN
    FOR rec IN SELECT id, document_number FROM documentation_unit WHERE documentation_office IS NULL LOOP
            RAISE NOTICE 'Defaulting documentation_office to BSG for document_number: % (ID: %)', rec.document_number, rec.id;
        END LOOP;
    UPDATE documentation_unit SET documentation_office = 'BSG' WHERE documentation_office IS NULL;
END $$;


-- Backfill 'documentation_unit_index' table
UPDATE literature_documentation_unit_index ldui
SET
    documentation_unit_type = du.documentation_unit_type,
    documentation_office = du.documentation_office
FROM documentation_unit du
WHERE ldui.documentation_unit_id = du.id
  AND (ldui.documentation_unit_type IS NULL OR ldui.documentation_office IS NULL);


-- Add constraints
ALTER TABLE document_number
    ALTER COLUMN prefix SET NOT NULL;

ALTER TABLE documentation_unit
    ALTER COLUMN documentation_unit_type SET NOT NULL;

ALTER TABLE documentation_unit
    ALTER COLUMN documentation_office SET NOT NULL;

ALTER TABLE literature_documentation_unit_index
    ALTER COLUMN documentation_unit_type SET NOT NULL;

ALTER TABLE literature_documentation_unit_index
    ALTER COLUMN documentation_office SET NOT NULL;


-- Update constraints on 'document_number' table
-- Drop the old unique constraints
ALTER TABLE document_number
DROP CONSTRAINT IF EXISTS document_number_uc;

-- Unique constraint on 'prefix' and 'year'
ALTER TABLE document_number
    ADD CONSTRAINT uk_document_number_prefix_year UNIQUE (prefix, year);
