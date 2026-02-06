CREATE TABLE IF NOT EXISTS
    active_reference_adm
(
    id                           uuid         NOT NULL
        CONSTRAINT active_reference_adm_pkey PRIMARY KEY,
    source_documentation_unit_id uuid         NOT NULL
        CONSTRAINT active_reference_adm_documentation_unit_id_fkey REFERENCES documentation_unit ON DELETE CASCADE,
    target_documentation_unit_id uuid,
    zitierart                    varchar(255) NOT NULL,
    normgeber                    text,
    inkrafttretedatum            text,
    aktenzeichen                 text,
    fundstelle                   text         NOT NULL,
    document_type                varchar(255),
    updated_at                   timestamp    NOT NULL,
    published_at                 timestamp    NOT NULL
);

CREATE INDEX idx_active_reference_adm ON active_reference_adm (source_documentation_unit_id);
