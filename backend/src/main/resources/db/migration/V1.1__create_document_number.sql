CREATE TABLE IF NOT EXISTS
    document_number
(
    id              uuid NOT NULL,
    latest          VARCHAR(255) NOT NULL,
    year            INTEGER NOT NULL,
    CONSTRAINT document_number_pkey PRIMARY KEY (id),
    CONSTRAINT document_number_uc UNIQUE (year)
);
