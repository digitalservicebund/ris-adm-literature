package de.bund.digitalservice.ris.adm_literature.adapter.persistence;

import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface CitationTypeRepository extends JpaRepository<CitationTypeEntity, UUID> {
  Page<CitationTypeEntity> findByAbbreviationContainingIgnoreCaseOrLabelContainingIgnoreCase(
    @Nonnull String abbreviation,
    @Nonnull String label,
    @Nonnull Pageable pageable
  );
}
