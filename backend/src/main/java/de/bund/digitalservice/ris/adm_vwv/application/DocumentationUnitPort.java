package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.Optional;

public interface DocumentationUnitPort {
  Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber);
  DocumentationUnit create();

  /**
   * Updates a documentation unit by document number and returns the updated documentation unit.
   *
   * @param documentNumber The document number to identify the documentation unit
   * @param json The json string to update
   * @return The updated documentation unit or an empty optional, if there is no documentation unit
   *     with the given document number
   */
  Optional<DocumentationUnit> update(@Nonnull String documentNumber, @Nonnull String json);
}
