set role lookup_tables;

CREATE TABLE
    IF NOT EXISTS
    document_type
(
    id UUID NOT NULL
        CONSTRAINT document_type_pkey PRIMARY KEY,
    abbreviation VARCHAR(32) NOT NULL,
    label VARCHAR(255) NOT NULL,
    category VARCHAR(1) NOT NULL,
    CONSTRAINT document_type_abbreviation_category_uc UNIQUE(abbreviation, category)
);

INSERT INTO lookup_tables.document_type VALUES ('b678b77b-ffc4-4756-825d-a4376b985b0d', 'VE', 'Verwaltungsvereinbarung', 'N');
INSERT INTO lookup_tables.document_type VALUES ('8de5e4a0-6b67-4d65-98db-efe877a260c4', 'VR', 'Verwaltungsregelung', 'N');
INSERT INTO lookup_tables.document_type VALUES ('77da35f0-aa4c-4ed3-9048-59b8e10f7478', 'VV', 'Verwaltungsvorschrift', 'N');

CREATE TABLE
    IF NOT EXISTS
    lookup_tables.field_of_law
(
    id         uuid          NOT NULL,
    identifier varchar(255)  NOT NULL,
    "text"     varchar(1000) NOT NULL,
    juris_id   int4          NOT NULL,
    "notation" varchar(255)  NOT NULL,
    field_of_law_parent_id UUID
        CONSTRAINT field_of_law_parent_fkey REFERENCES field_of_law,
    CONSTRAINT field_of_law_identifier_key UNIQUE (identifier),
    CONSTRAINT field_of_law_juris_id_notation_key UNIQUE (juris_id, "notation"),
    CONSTRAINT field_of_law_pkey PRIMARY KEY (id)
);


CREATE
    INDEX ON
    lookup_tables.field_of_law (field_of_law_parent_id);

CREATE TABLE
    IF NOT EXISTS
    lookup_tables.field_of_law_field_of_law_text_reference
(
    field_of_law_id                UUID NOT NULL
        CONSTRAINT field_of_law_fkey REFERENCES field_of_law,
    field_of_law_text_reference_id UUID NOT NULL
        CONSTRAINT field_of_law_text_reference_fkey REFERENCES field_of_law,
    CONSTRAINT field_of_law_field_of_law_text_reference_pkey PRIMARY KEY (
      field_of_law_id,
      field_of_law_text_reference_id
    )
);

CREATE
    INDEX ON
    lookup_tables.field_of_law_field_of_law_text_reference (field_of_law_text_reference_id);

CREATE TABLE
    IF NOT EXISTS
    lookup_tables.field_of_law_keyword
(
    id    UUID         NOT NULL
        CONSTRAINT field_of_law_keyword_pkey PRIMARY KEY,
    value VARCHAR(255) NOT NULL
        CONSTRAINT field_of_law_keyword_key UNIQUE
);

CREATE TABLE
    IF NOT EXISTS
    lookup_tables.field_of_law_field_of_law_keyword
(
    field_of_law_id         UUID NOT NULL
        CONSTRAINT field_of_law_fkey REFERENCES field_of_law,
    field_of_law_keyword_id UUID NOT NULL
        CONSTRAINT field_of_law_keyword_fkey REFERENCES field_of_law_keyword,
    CONSTRAINT field_of_law_field_of_law_keyword_pkey PRIMARY KEY (
       field_of_law_id,
       field_of_law_keyword_id
    )
);

CREATE
    INDEX ON
    lookup_tables.field_of_law_field_of_law_keyword (field_of_law_keyword_id);

CREATE
    TABLE
    IF NOT EXISTS
    lookup_tables.field_of_law_navigation_term
(
    id    UUID         NOT NULL
        CONSTRAINT field_of_law_navigation_term_pkey PRIMARY KEY,
    value VARCHAR(255) NOT NULL
        CONSTRAINT field_of_law_navigation_term_key UNIQUE
);

CREATE
    TABLE
    IF NOT EXISTS
    lookup_tables.field_of_law_field_of_law_navigation_term
(
    field_of_law_id                 UUID NOT NULL
        CONSTRAINT field_of_law_fkey REFERENCES field_of_law,
    field_of_law_navigation_term_id UUID NOT NULL
        CONSTRAINT field_of_law_navigation_term_fkey REFERENCES field_of_law_navigation_term,
    CONSTRAINT field_of_law_field_of_law_navigation_term_pkey PRIMARY KEY (
                                                                           field_of_law_id,
                                                                           field_of_law_navigation_term_id
        )
);

CREATE
    INDEX ON
    lookup_tables.field_of_law_field_of_law_navigation_term (field_of_law_navigation_term_id);

CREATE
    TABLE
    IF NOT EXISTS
    lookup_tables.field_of_law_norm
(
    id                      UUID         NOT NULL
        CONSTRAINT field_of_law_norm_pkey PRIMARY KEY,
    abbreviation            VARCHAR(255) NOT NULL,
    single_norm_description VARCHAR(255),
    field_of_law_id         UUID         NOT NULL
        CONSTRAINT field_of_law_fkey REFERENCES field_of_law
);

CREATE
    INDEX ON
    lookup_tables.field_of_law_norm (field_of_law_id);

INSERT INTO lookup_tables.field_of_law
(id, identifier, "text", juris_id, "notation")
VALUES('a785fb96-a45d-4d4c-8d9c-92d8a6592b22', 'PR', 'Phantasierecht', 12345, 'NEW');
INSERT INTO lookup_tables.field_of_law
(id, identifier, "text", juris_id, "notation", "field_of_law_parent_id")
VALUES('b3213dee-a986-4807-9ef3-03a3ed32c45a', 'PR-05', 'Beendigung der Phantasieverhältnisse', 12346, 'NEW', 'a785fb96-a45d-4d4c-8d9c-92d8a6592b22');
INSERT INTO lookup_tables.field_of_law
(id, identifier, "text", juris_id, "notation", "field_of_law_parent_id")
VALUES('9c06a4e1-02a0-4a73-b721-45ea0d98429b', 'PR-05-01', 'Phantasie besonderer Art, Ansprüche anderer Art', 12347, 'NEW', 'b3213dee-a986-4807-9ef3-03a3ed32c45a');

INSERT INTO lookup_tables.field_of_law_norm
(id, abbreviation, single_norm_description, field_of_law_id)
VALUES('d74ab1e8-3ebe-4571-98b7-852e3b07e3c1', 'PStG', '§ 99', 'b3213dee-a986-4807-9ef3-03a3ed32c45a');

INSERT INTO lookup_tables.field_of_law
(id, identifier, "text", juris_id, "notation")
VALUES('f478913c-979b-4e34-843c-441b9f559899', 'XX', 'Anderesrecht', 12348, 'NEW');
INSERT INTO lookup_tables.field_of_law
(id, identifier, "text", juris_id, "notation", "field_of_law_parent_id")
VALUES('14728419-119a-40f7-8f9a-47ec342c6286', 'XX-03', 'Arbeitsförderungsrecht; Versicherungspflicht oder -freiheit siehe XX-04-02 oder XX-04-03;- Beitragsrecht siehe XX-04-05', 12349, 'NEW', 'f478913c-979b-4e34-843c-441b9f559899');
INSERT INTO lookup_tables.field_of_law
(id, identifier, "text", juris_id, "notation")
VALUES('5eaf2263-2717-4375-8ce2-0c45fc10eaaa', 'XX-04-02', 'Versicherter Personenkreis: Versicherungspflicht und Beitragspflicht; Beginn, Ende, Fortbestand der Mitgliedschaft siehe XX-05-07-04 bis -05', 12350, 'NEW');
INSERT INTO lookup_tables.field_of_law
(id, identifier, "text", juris_id, "notation")
VALUES
('7e0b1fe7-fe65-4cba-a282-5ed52d2ce32a', 'test-0', 'test-0', 90000, 'NEW'),
('3951b3f9-fb58-4501-9670-ae7531b5b173', 'test-1', 'test-1', 90001, 'NEW'),
('23e7c5c8-0cfc-4d18-a259-cd79d76c2b02', 'test-2', 'test-2', 90002, 'NEW'),
('3541cd6f-9360-446a-ad9f-f9ebd5a5ec21', 'test-3', 'test-3', 90003, 'NEW'),
('f02ac7fb-8d2d-46bf-821a-e748d3466497', 'test-4', 'test-4', 90004, 'NEW'),
('3b7633a7-0452-446c-a82a-ccf866e3c75e', 'test-5', 'test-5', 90005, 'NEW'),
('ff49f0f5-a44d-48b5-ac13-810edaecd9d6', 'test-6', 'test-6', 90006, 'NEW'),
('56c5d5df-4572-4ec0-a672-7f85aa48fd80', 'test-7', 'test-7', 90007, 'NEW'),
('d6763f52-9aaa-4eb8-a59d-58dd94a10651', 'test-8', 'test-8', 90008, 'NEW'),
('0f1d3881-16f4-4d30-af35-3dc6ab89308b', 'test-9', 'test-9', 90009, 'NEW');
INSERT INTO lookup_tables.field_of_law_field_of_law_text_reference
(field_of_law_id, field_of_law_text_reference_id)
VALUES('14728419-119a-40f7-8f9a-47ec342c6286', '5eaf2263-2717-4375-8ce2-0c45fc10eaaa');

CREATE TABLE
  IF NOT EXISTS
  documentation_office
(
  id           UUID NOT NULL
    CONSTRAINT documentation_office_pkey PRIMARY KEY,
  abbreviation VARCHAR(6)
    CONSTRAINT documentation_office_abbreviation_uc UNIQUE,
  jurisdiction_type_id UUID
);

CREATE TABLE
  IF NOT EXISTS
  legal_periodical
(
  id                                          UUID         NOT NULL
    CONSTRAINT legal_periodical_pkey PRIMARY KEY,
  abbreviation                                VARCHAR(255) NOT NULL,
  title                                       VARCHAR(255),
  subtitle                                    VARCHAR(255),
  primary_reference                           BOOLEAN,
  evaluation                                  BOOLEAN,
  remark                                      TEXT,
  publisher                                   VARCHAR(255),
  editor                                      VARCHAR(255),
  publications_per_year                       INTEGER,
  citation_style                              VARCHAR(255),
  responsible_documentation_office_caselaw_id UUID
    CONSTRAINT documentation_office_fkey REFERENCES documentation_office,
  published_from                              VARCHAR(4),
  published_to                                VARCHAR(4),
  literature_evaluation_from                  VARCHAR(4),
  literature_evaluation_to                    VARCHAR(4),
  literature_evaluation_complete              VARCHAR(4),
  caselaw_evaluation_from                     VARCHAR(4),
  caselaw_evaluation_to                       VARCHAR(4),
  caselaw_evaluation_complete                 VARCHAR(4),
  attachment_to                               VARCHAR(10),
  juris_id                                    VARCHAR(255) NOT NULL
    CONSTRAINT legal_periodical_juris_id_uc UNIQUE
);

CREATE INDEX
  IF NOT EXISTS
  legal_periodical_documentation_office_caselaw_idx ON
  legal_periodical (responsible_documentation_office_caselaw_id);

INSERT INTO legal_periodical (id, abbreviation, title, subtitle, citation_style, juris_id) VALUES
(gen_random_uuid(), 'BKK', 'Die Betriebskrankenkasse', 'Zeitschrift des Bundesverbandes der Betriebskrankenkassen Essen', '1969, 138-140; BKK 2007, Sonderbeilage, 1-5', '9999'),
(gen_random_uuid(), 'ABc', 'Die Beispieler', 'Zeitschrift des Beispiels', 'ab ab ab ab abc', '999');

CREATE TABLE
    IF NOT EXISTS
    region
(
    id   UUID NOT NULL
        CONSTRAINT region_pkey PRIMARY KEY,
    code VARCHAR(255)
        CONSTRAINT region_code_uc UNIQUE,
    long_text TEXT,
    applicability BOOLEAN
);

CREATE TABLE
    IF NOT EXISTS
    institution
(
    id              UUID         NOT NULL
        CONSTRAINT institution_pkey PRIMARY KEY,
    name            TEXT         NOT NULL,
    official_name   TEXT,
    foreign_country VARCHAR(10),
    juris_id        VARCHAR(255) NOT NULL,
    type            VARCHAR(10),
    CONSTRAINT institution_juris_id_type_uc UNIQUE (juris_id, type)
);

CREATE INDEX
    IF NOT EXISTS
    institution_name_idx ON
    institution (name);

CREATE TABLE
    IF NOT EXISTS
    institution_region
(
    institution_id UUID NOT NULL
        CONSTRAINT institution_fkey REFERENCES institution,
    region_id      UUID NOT NULL
        CONSTRAINT region_fkey REFERENCES region,
    CONSTRAINT institution_region_pkey PRIMARY KEY (
                                                    institution_id,
                                                    region_id
        )
);

CREATE INDEX
    IF NOT EXISTS
    institution_region_idx ON
    institution_region (region_id);

INSERT INTO region (id, code) VALUES
           ('14af4110-fa58-4753-a949-6b6ba2dbcfe1', 'AA'),
           ('01c63c40-74b4-486a-8914-083e7f03baef', 'BB'),
           ('0e2b9b4d-4a8f-4124-a927-14eded513bee', 'CC');
INSERT INTO institution (id, name, official_name, foreign_country, juris_id, type) VALUES
           (gen_random_uuid(), 'Erstes Organ', 'Organ Eins', NULL, '1', 'organ'),
           (gen_random_uuid(), 'Zweites Organ', NULL, NULL, '2', 'organ'),
           ('bddafc08-4031-4960-9aad-0868d48323f7', 'Erste Jurpn', 'Jurpn Eins', 'Nein', '1', 'jurpn'),
           (gen_random_uuid(), 'Zweite Jurpn', NULL, 'Ja', '2', 'jurpn'),
           ('32b0a3fd-e340-4f9b-bd39-7fe2dcff9a75', 'Dritte Jurpn', NULL, 'Nein', '3', 'jurpn'),
           (gen_random_uuid(), 'JurpnOrgan', NULL, 'Nein', '4', 'jurpn'),
           (gen_random_uuid(), 'JurpnOrgan', NULL, 'Nein', '5', 'organ');
INSERT INTO institution_region (institution_id, region_id) VALUES
        ('bddafc08-4031-4960-9aad-0868d48323f7', '14af4110-fa58-4753-a949-6b6ba2dbcfe1'),
        ('bddafc08-4031-4960-9aad-0868d48323f7', '01c63c40-74b4-486a-8914-083e7f03baef'),
        ('32b0a3fd-e340-4f9b-bd39-7fe2dcff9a75', '0e2b9b4d-4a8f-4124-a927-14eded513bee');



set role test;
