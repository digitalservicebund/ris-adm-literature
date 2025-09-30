ALTER TABLE document_number ADD COLUMN document_type VARCHAR(255);

UPDATE document_number SET document_type = 'ADM_VWV';

ALTER TABLE document_number ALTER COLUMN document_type SET NOT NULL;
ALTER TABLE document_number DROP CONSTRAINT document_number_uc;

ALTER TABLE document_number ADD CONSTRAINT uk_document_number_year_type UNIQUE (year, document_type);
