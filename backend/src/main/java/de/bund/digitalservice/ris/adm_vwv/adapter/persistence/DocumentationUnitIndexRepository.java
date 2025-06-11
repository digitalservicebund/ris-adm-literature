package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentationUnitIndexRepository
  extends JpaRepository<DocumentationUnitIndexEntity, UUID> {
  Optional<DocumentationUnitIndexEntity> findByDocumentationUnitId(
    @Nonnull UUID documentationUnitId
  );
}
