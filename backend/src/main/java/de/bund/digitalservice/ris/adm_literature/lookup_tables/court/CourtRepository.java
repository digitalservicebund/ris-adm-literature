package de.bund.digitalservice.ris.adm_literature.lookup_tables.court;

import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface CourtRepository extends JpaRepository<CourtEntity, UUID> {
  Page<CourtEntity> findByTypeContainingIgnoreCaseOrLocationContainingIgnoreCase(
    @Nonnull String type,
    @Nonnull String location,
    @Nonnull Pageable pageable
  );
}
