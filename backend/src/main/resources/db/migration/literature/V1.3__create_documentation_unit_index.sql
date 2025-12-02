CREATE TABLE IF NOT EXISTS
    literature_documentation_unit_index
(
    id              uuid NOT NULL
        CONSTRAINT literature_documentation_unit_index_pkey PRIMARY KEY,
    documentation_unit_id uuid NOT NULL
        CONSTRAINT literature_documentation_unit_index_fkey REFERENCES documentation_unit,
    titel text
);
