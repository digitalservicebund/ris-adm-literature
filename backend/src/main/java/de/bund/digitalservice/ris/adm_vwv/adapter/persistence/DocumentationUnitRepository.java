package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.annotation.Nonnull;
import java.time.Year;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentationUnitRepository extends JpaRepository<DocumentationUnitEntity, UUID> {
  Optional<DocumentationUnitEntity> findByDocumentNumber(@Nonnull String documentNumber);

  Optional<DocumentationUnitEntity> findTopByYearOrderByDocumentNumberDesc(Year year);
}
