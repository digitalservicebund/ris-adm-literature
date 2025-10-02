ALTER TABLE document_number ADD COLUMN documentation_office VARCHAR(255);
ALTER TABLE document_number ADD COLUMN document_type_code VARCHAR(255);

ALTER TABLE document_number ALTER COLUMN documentation_office SET NOT NULL;
ALTER TABLE document_number ALTER COLUMN document_type_code SET NOT NULL;

ALTER TABLE document_number DROP CONSTRAINT document_number_uc;
ALTER TABLE document_number ADD CONSTRAINT uq_doc_num_year_office_type UNIQUE (year, documentation_office, document_type_code);
