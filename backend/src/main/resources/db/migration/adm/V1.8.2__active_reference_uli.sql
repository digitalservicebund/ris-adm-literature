CREATE TABLE IF NOT EXISTS
    active_reference_uli
(
    id                           uuid         NOT NULL
        CONSTRAINT active_reference_uli_pkey PRIMARY KEY,
    source_documentation_unit_id uuid         NOT NULL
        CONSTRAINT active_reference_uli_documentation_unit_id_fkey REFERENCES documentation_unit ON DELETE CASCADE,
    target_documentation_unit_id uuid,
    zitierart                    varchar(255) NOT NULL,
    fundstelle                   text         NOT NULL,
    verfasser                    text[]       NOT NULL,
    dokumenttypen                text[],
    updated_at                   timestamp    NOT NULL,
    published_at                 timestamp    NOT NULL
);

CREATE INDEX idx_active_reference_uli ON active_reference_uli (source_documentation_unit_id);
