CREATE SCHEMA 
IF NOT EXISTS
ris_lookup_tables;

CREATE TABLE 
IF NOT EXISTS
ris_lookup_tables.document_types (
    id uuid not null,
    abbreviation character varying(255),
    name character varying(255),
    constraint document_types_pkey primary key (id)
);
