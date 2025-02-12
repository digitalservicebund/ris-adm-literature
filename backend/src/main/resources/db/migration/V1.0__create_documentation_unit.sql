CREATE TABLE IF NOT EXISTS
    documentation_unit
(
    id              uuid NOT NULL,
    document_number varchar(255),
    json            text,
    CONSTRAINT documentation_unit_pkey PRIMARY KEY (id)
);
