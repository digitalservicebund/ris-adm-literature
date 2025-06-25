package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

/**
 * Input port for CRUD operations on documentation units.
 */
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

  /**
   * Returns paginated documentation units overview elements.
   * @param query The query
   * @return Page object with documentation unit overview elements and pagination data
   */
  Page<DocumentationUnitOverviewElement> findDocumentationUnitOverviewElements(
    @Nonnull DocumentationUnitQuery query,
    @Nonnull Pageable pageable
  );
}
