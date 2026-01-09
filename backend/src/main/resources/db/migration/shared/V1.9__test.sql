SET ROLE references_schema;

CREATE TABLE IF NOT EXISTS references_schema.document_reference (
                                                                    id UUID PRIMARY KEY,
                                                                    adm_document_number VARCHAR
                                                                    CONSTRAINT fk_document_reference_adm REFERENCES adm.documentation_unit (document_number),
    literature_document_number VARCHAR
    CONSTRAINT fk_document_reference_literature REFERENCES literature.documentation_unit (document_number),
    document_category VARCHAR(255),
    CONSTRAINT check_exactly_one_fk CHECK (
(CASE WHEN adm_document_number IS NOT NULL THEN 1 ELSE 0 END +
 CASE WHEN literature_document_number IS NOT NULL THEN 1 ELSE 0 END) = 1
    )
    );

CREATE TABLE IF NOT EXISTS references_schema.active_reference (
                                                                  id UUID PRIMARY KEY,
                                                                  source_id UUID NOT NULL
                                                                  CONSTRAINT fk_active_reference_source REFERENCES references_schema.document_reference (id),
    target_id UUID NOT NULL
    CONSTRAINT fk_active_reference_target REFERENCES references_schema.document_reference (id),
    CONSTRAINT uc_active_reference UNIQUE (source_id, target_id)
    );

CREATE OR REPLACE VIEW references_schema.adm_passive_reference AS
SELECT
    source.id AS source_id,
    source.document_category,
    CASE WHEN source.document_category = 'LITERATUR_SELBSTAENDIG' OR source.document_category = 'LITERATUR_UNSELBSTAENDIG' THEN source.literature_document_number
         WHEN source.document_category = 'VERWALTUNGSVORSCHRIFTEN' THEN source.adm_document_number END AS source_document_number,
        END AS source_document_number,
    target.id AS target_id,
    target.adm_document_number AS target_document_number
FROM references_schema.active_reference ar
         JOIN references_schema.document_reference source ON ar.source_id = source.id
         JOIN references_schema.document_reference target ON ar.target_id = target.id
WHERE target.document_category = 'VERWALTUNGSVORSCHRIFTEN';

SET ROLE postgres;
