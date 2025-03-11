set role lookup_tables;

create table lookup_tables.document_type
(
    id uuid not null,
    name varchar(255) not null,
    constraint document_type_pkey primary key (id)
);

INSERT INTO lookup_tables.document_type VALUES ('8de5e4a0-6b67-4d65-98db-efe877a260c4', 'VR');

set role test;
