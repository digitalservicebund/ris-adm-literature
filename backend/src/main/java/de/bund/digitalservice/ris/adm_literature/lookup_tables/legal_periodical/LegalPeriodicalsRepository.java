package de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical;

import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface LegalPeriodicalsRepository extends JpaRepository<LegalPeriodicalEntity, UUID> {
  Page<LegalPeriodicalEntity> findByAbbreviationContainingIgnoreCaseOrTitleContainingIgnoreCase(
    @Nonnull String abbreviation,
    @Nonnull String title,
    @Nonnull Pageable pageable
  );
}
