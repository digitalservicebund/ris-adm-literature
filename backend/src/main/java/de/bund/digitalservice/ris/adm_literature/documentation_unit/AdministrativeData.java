package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;

/**
 * Administrative data record which encapsulates all data which are not part of a documentation unit content.
 * @param documentCategory The document category
 * @param note The note
 */
public record AdministrativeData(DocumentCategory documentCategory, String note) {}
