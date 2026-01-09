package de.bund.digitalservice.ris.adm_literature.documentation_unit.references;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;

public record ReferenceItem(String documentNumber, DocumentCategory documentCategory) {}
