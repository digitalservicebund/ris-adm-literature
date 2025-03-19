package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentTypesRepository extends JpaRepository<DocumentTypeEntity, UUID> {
  Page<DocumentTypeEntity> findByAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
    @Nonnull String abbreviation,
    @Nonnull String name,
    @Nonnull Pageable pageable
  );
}
