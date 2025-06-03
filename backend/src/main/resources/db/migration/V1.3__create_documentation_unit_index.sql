CREATE TABLE IF NOT EXISTS
    documentation_unit_index
(
    id              uuid NOT NULL
        CONSTRAINT documentation_unit_index_pkey PRIMARY KEY,
    documentation_unit_id uuid NOT NULL
        CONSTRAINT documentation_unit_index_fkey REFERENCES documentation_unit,
    langueberschrift text,
    fundstellen text,
    zitierdaten text
);
