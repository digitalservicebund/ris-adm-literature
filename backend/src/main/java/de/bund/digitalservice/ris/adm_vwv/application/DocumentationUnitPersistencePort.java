package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

/**
 * Output persistence port for CRUD operations on documentation units.
 */
public interface DocumentationUnitPersistencePort {
  Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber);

  DocumentationUnit create();

  DocumentationUnit update(@Nonnull String documentNumber, @Nonnull String json);

  Page<DocumentationUnitOverviewElement> findDocumentationUnitOverviewElements(
    @Nonnull DocumentationUnitQuery query,
    @Nonnull Pageable pageable
  );
}
