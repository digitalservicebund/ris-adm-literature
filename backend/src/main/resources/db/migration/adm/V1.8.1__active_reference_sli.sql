CREATE TABLE IF NOT EXISTS
    active_reference_sli
(
    id                           uuid         NOT NULL
        CONSTRAINT active_reference_sli_pkey PRIMARY KEY,
    source_documentation_unit_id uuid         NOT NULL
        CONSTRAINT active_reference_sli_documentation_unit_id_fkey REFERENCES documentation_unit ON DELETE CASCADE,
    target_documentation_unit_id uuid,
    target_document_number       varchar(255),
    zitierart                    varchar(255) NOT NULL,
    titel                        text,
    veroeffentlichungsjahr       text,
    dokumenttypen                text[],
    verfasser                    text[],
    updated_at                   timestamp    NOT NULL,
    published_at                 timestamp    NOT NULL
);

CREATE UNIQUE INDEX uc_active_reference_sli ON active_reference_sli (source_documentation_unit_id, target_documentation_unit_id, zitierart) where
    target_documentation_unit_id IS NOT NULL;

CREATE INDEX idx_active_reference_sli ON active_reference_sli (source_documentation_unit_id);
