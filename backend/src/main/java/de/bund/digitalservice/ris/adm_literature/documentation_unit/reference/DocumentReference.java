package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import org.jspecify.annotations.NonNull;

/**
 * A document reference points to a concrete documentation unit and consists of
 * a document number and a document category.
 *
 * @param documentNumber The document number
 * @param documentCategory The document category
 */
public record DocumentReference(
  @NonNull String documentNumber,
  @NonNull DocumentCategory documentCategory
) {}
