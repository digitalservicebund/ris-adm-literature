package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitContent;
import org.jspecify.annotations.NonNull;

/**
 * Common interface for all documentation unit content types.
 */
public interface DocumentationUnitContent {
  String documentNumber();
  DocumentCategory documentCategory();
  String note();

  /**
   * Returns the specific documentation unit content class type for the given document category.
   * @param documentCategory The document category
   * @return Documentation unit content class type
   */
  static Class<? extends DocumentationUnitContent> getDocumentationUnitContentClass(
    @NonNull DocumentCategory documentCategory
  ) {
    return switch (documentCategory) {
      case VERWALTUNGSVORSCHRIFTEN -> AdmDocumentationUnitContent.class;
      case LITERATUR_SELBSTAENDIG -> SliDocumentationUnitContent.class;
      case LITERATUR_UNSELBSTAENDIG -> UliDocumentationUnitContent.class;
      default -> throw new IllegalArgumentException("Unexpected value: " + documentCategory);
    };
  }
}
