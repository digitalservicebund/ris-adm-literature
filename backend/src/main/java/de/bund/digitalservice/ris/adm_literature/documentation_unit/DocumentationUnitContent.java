package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;

/**
 * Common interface for all documentation unit content types.
 */
public interface DocumentationUnitContent {
  String documentNumber();
  DocumentCategory documentCategory();
  String note();
}
