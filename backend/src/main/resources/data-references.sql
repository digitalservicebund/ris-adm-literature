ALTER TABLE document_reference
ADD CONSTRAINT fk_document_reference_adm FOREIGN KEY (adm_document_number) REFERENCES adm.documentation_unit (document_number),
ADD CONSTRAINT fk_document_reference_literature FOREIGN KEY (literature_document_number) REFERENCES literature.documentation_unit (document_number);
