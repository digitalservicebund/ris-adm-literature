CREATE TABLE IF NOT EXISTS
    documentation_unit_note
(
    id              uuid NOT NULL
        CONSTRAINT documentation_unit_note_pkey PRIMARY KEY,
    documentation_unit_id uuid UNIQUE NOT NULL
        CONSTRAINT documentation_unit_note_fkey REFERENCES documentation_unit ON DELETE CASCADE,
    note text NOT NULL
);
