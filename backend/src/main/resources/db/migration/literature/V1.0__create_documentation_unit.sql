CREATE TABLE IF NOT EXISTS
    documentation_unit
(
    id              uuid NOT NULL,
    document_number VARCHAR(255) NOT NULL,
    json            text,
    CONSTRAINT documentation_unit_pkey PRIMARY KEY (id),
    CONSTRAINT documentation_unit_uc UNIQUE (document_number)
);
