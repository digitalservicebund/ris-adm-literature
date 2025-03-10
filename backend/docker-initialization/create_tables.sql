set role ris_wertetabellen;

create table ris_wertetabellen.document_type
(
    id uuid not null,
    name varchar(255) not null,
    constraint document_type_pkey primary key (id)
);

INSERT INTO ris_wertetabellen.document_type VALUES ('8de5e4a0-6b67-4d65-98db-efe877a260c4', 'VR');

set role test;
