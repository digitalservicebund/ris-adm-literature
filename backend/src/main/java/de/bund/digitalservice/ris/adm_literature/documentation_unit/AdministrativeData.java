package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;

public record AdministrativeData(DocumentCategory documentCategory, String note) {}
