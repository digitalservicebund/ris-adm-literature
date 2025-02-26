package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface DocumentationUnitRepository extends JpaRepository<DocumentationUnitEntity, UUID> {
  Optional<DocumentationUnitEntity> findByDocumentNumber(@Nonnull String documentNumber);

}
