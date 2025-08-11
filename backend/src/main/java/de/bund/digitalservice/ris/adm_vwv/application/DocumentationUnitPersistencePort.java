package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.Optional;

/**
 * Output persistence port for CRUD operations on documentation units.
 */
public interface DocumentationUnitPersistencePort {
  Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber);

  DocumentationUnit create();

  DocumentationUnit update(@Nonnull String documentNumber, @Nonnull String json);

  DocumentationUnit publish(@Nonnull String documentNumber, @Nonnull String xml);

  Page<DocumentationUnitOverviewElement> findDocumentationUnitOverviewElements(
    @Nonnull DocumentationUnitQuery query
  );
}
