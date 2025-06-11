package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface DocumentationUnitRepository
  extends
    JpaRepository<DocumentationUnitEntity, UUID>,
    JpaSpecificationExecutor<DocumentationUnitEntity> {
  Optional<DocumentationUnitEntity> findByDocumentNumber(@Nonnull String documentNumber);

  @EntityGraph(attributePaths = "documentationUnitIndex")
  Slice<DocumentationUnitEntity> findByDocumentationUnitIndexIsNull(Pageable pageable);
}
