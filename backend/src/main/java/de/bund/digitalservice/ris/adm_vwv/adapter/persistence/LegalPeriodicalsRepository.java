package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.UUID;

interface LegalPeriodicalsRepository extends JpaRepository<LegalPeriodicalEntity, UUID> {
  Page<LegalPeriodicalEntity> findByAbbreviationContainingIgnoreCaseOrTitleContainingIgnoreCase(
    @Nonnull String abbreviation,
    @Nonnull String title,
    @Nonnull Pageable pageable
  );
}
