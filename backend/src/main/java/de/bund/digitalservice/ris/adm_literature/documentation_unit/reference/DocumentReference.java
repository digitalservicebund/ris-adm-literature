package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

/**
 * A document reference points to a concrete documentation unit and consists of
 * a documentation unit id and a document category.
 *
 * @param documentationUnitId The documentation unit id
 * @param documentCategory The document category
 */
public record DocumentReference(
  @NonNull UUID documentationUnitId,
  @NonNull DocumentCategory documentCategory
) {}
