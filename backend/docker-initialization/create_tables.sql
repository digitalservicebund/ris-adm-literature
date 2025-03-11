set role lookup_tables;

CREATE TABLE
IF NOT EXISTS
lookup_tables.document_types (
    id uuid not null,
    abbreviation character varying(255),
    name character varying(255),
    constraint document_types_pkey primary key (id)
);

INSERT INTO lookup_tables.document_types VALUES ('b678b77b-ffc4-4756-825d-a4376b985b0d', 'VE', 'Verwaltungsvereinbarung');
INSERT INTO lookup_tables.document_types VALUES ('8de5e4a0-6b67-4d65-98db-efe877a260c4', 'VR', 'Verwaltungsregelung');

set role test;
