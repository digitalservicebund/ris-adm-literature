package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.Optional;

/**
 * Port for CRUD operations on DocumentationUnits
 */
public interface DocumentationUnitPersistencePort {
  Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber);

  DocumentationUnit create();

  DocumentationUnit update(@Nonnull String documentNumber, @Nonnull String json);
}
