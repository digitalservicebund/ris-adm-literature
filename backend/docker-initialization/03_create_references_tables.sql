SET ROLE references_schema;

CREATE TABLE IF NOT EXISTS references_schema.document_reference
(
    id                         UUID PRIMARY KEY,
    adm_document_number        VARCHAR,
    literature_document_number VARCHAR,
    document_category          VARCHAR(255),
    CONSTRAINT check_exactly_one_fk CHECK (
        (CASE WHEN adm_document_number IS NOT NULL THEN 1 ELSE 0 END +
         CASE WHEN literature_document_number IS NOT NULL THEN 1 ELSE 0 END) = 1
        )
);

-- Enforce uniqueness for ADM within category
CREATE UNIQUE INDEX IF NOT EXISTS uq_document_reference_adm_cat
    ON references_schema.document_reference (adm_document_number, document_category)
    WHERE adm_document_number IS NOT NULL;

-- Enforce uniqueness for Literature within category
CREATE UNIQUE INDEX IF NOT EXISTS uq_document_reference_lit_cat
    ON references_schema.document_reference (literature_document_number, document_category)
    WHERE literature_document_number IS NOT NULL;

CREATE TABLE IF NOT EXISTS references_schema.active_reference
(
    id        UUID PRIMARY KEY,
    source_id UUID NOT NULL
        CONSTRAINT fk_active_reference_source REFERENCES references_schema.document_reference (id),
    target_id UUID NOT NULL
        CONSTRAINT fk_active_reference_target REFERENCES references_schema.document_reference (id),
    CONSTRAINT uc_active_reference UNIQUE (source_id, target_id)
);

CREATE OR REPLACE VIEW references_schema.adm_passive_reference AS
select source.id                                   as source_id,
       source.document_category,
       case
           when source.document_category = 'LITERATUR_SELBSTAENDIG' or
                source.document_category = 'LITERATUR_UNSELBSTAENDIG' then source.literature_document_number
           when source.document_category = 'VERWALTUNGSVORSCHRIFTEN'
               then source.adm_document_number end as source_document_number,
       target.id                                   as target_id,
       target.adm_document_number                  as target_document_number
from active_reference ar
         join document_reference source on ar.source_id = source.id
         join document_reference target on ar.target_id = target.id
where target.document_category = 'VERWALTUNGSVORSCHRIFTEN';

CREATE OR REPLACE VIEW references_schema.sli_passive_reference AS
select source.id                                   as source_id,
       source.document_category,
       case
           when source.document_category = 'LITERATUR_SELBSTAENDIG' or
                source.document_category = 'LITERATUR_UNSELBSTAENDIG' then source.literature_document_number
           when source.document_category = 'VERWALTUNGSVORSCHRIFTEN'
               then source.adm_document_number end as source_document_number,
       target.id                                   as target_id,
       target.adm_document_number                  as target_document_number
from active_reference ar
         join document_reference source on ar.source_id = source.id
         join document_reference target on ar.target_id = target.id
where target.document_category = 'LITERATUR_SELBSTAENDIG';

CREATE OR REPLACE VIEW references_schema.uli_passive_reference AS
select source.id                                   as source_id,
       source.document_category,
       case
           when source.document_category = 'LITERATUR_SELBSTAENDIG' or
                source.document_category = 'LITERATUR_UNSELBSTAENDIG' then source.literature_document_number
           when source.document_category = 'VERWALTUNGSVORSCHRIFTEN'
               then source.adm_document_number end as source_document_number,
       target.id                                   as target_id,
       target.adm_document_number                  as target_document_number
from active_reference ar
         join document_reference source on ar.source_id = source.id
         join document_reference target on ar.target_id = target.id
where target.document_category = 'LITERATUR_UNSELBSTAENDIG';

RESET ROLE;
