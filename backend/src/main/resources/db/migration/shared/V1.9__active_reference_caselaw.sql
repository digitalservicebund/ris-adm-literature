CREATE TABLE IF NOT EXISTS
    active_reference_caselaw
(
    id                           uuid         NOT NULL
        CONSTRAINT active_reference_caselaw_pkey PRIMARY KEY,
    source_documentation_unit_id uuid         NOT NULL
        CONSTRAINT active_reference_caselaw_documentation_unit_id_fkey REFERENCES documentation_unit ON DELETE CASCADE,
    target_documentation_unit_id uuid         NOT NULL,
    target_document_category     varchar(255) NOT NULL,
    zitierart                    varchar(255) NOT NULL,
    court_type                   varchar(255) NOT NULL,
    court_location               varchar(255) NOT NULL,
    decision_date                date         NOT NULL,
    aktenzeichen                 text         NOT NULL,
    document_number              varchar(255),
    document_type                varchar(255),
    updated_at                   timestamp    NOT NULL,
    published_at                 timestamp    NOT NULL,
    CONSTRAINT uc_active_reference UNIQUE (source_documentation_unit_id, target_documentation_unit_id, zitierart)
);

CREATE INDEX idx_active_reference_caselaw ON active_reference_caselaw (source_documentation_unit_id);
