package de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;

/**
 * The query business object used for looking up document types
 *
 * @param searchTerm   String to search for in document types
 * @param documentCategory The document category to search for in document types
 * @param queryOptions Details on pagination and sorting
 */
public record DocumentTypeQuery(
  String searchTerm,
  @Nonnull DocumentCategory documentCategory,
  @Nonnull QueryOptions queryOptions
) {}
