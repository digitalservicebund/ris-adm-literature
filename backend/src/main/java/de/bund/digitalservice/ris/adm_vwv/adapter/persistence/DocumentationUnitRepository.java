package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentationUnitRepository extends JpaRepository<DocumentationUnitEntity, UUID> {
  Optional<DocumentationUnitEntity> findByDocumentNumber(@Nonnull String documentNumber);

  Page<DocumentationUnitEntity> findByJsonIsNotNull(Pageable pageable);

  @EntityGraph(attributePaths = "documentationUnitIndex")
  List<DocumentationUnitEntity> findByDocumentationUnitIndexIsNull();
}
