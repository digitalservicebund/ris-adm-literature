CREATE TABLE IF NOT EXISTS
    active_reference_sli
(
    id                           uuid         NOT NULL
        CONSTRAINT active_reference_sli_pkey PRIMARY KEY,
    source_documentation_unit_id uuid         NOT NULL
        CONSTRAINT active_reference_sli_documentation_unit_id_fkey REFERENCES documentation_unit ON DELETE CASCADE,
    target_documentation_unit_id uuid,
    zitierart                    varchar(255) NOT NULL,
    titel                        text,
    veroeffentlichungsjahr       text,
    dokumenttypen                text[],
    verfasser                    text[],
    updated_at                   timestamp    NOT NULL,
    published_at                 timestamp    NOT NULL
);

CREATE INDEX idx_active_reference_sli ON active_reference_sli (source_documentation_unit_id);
