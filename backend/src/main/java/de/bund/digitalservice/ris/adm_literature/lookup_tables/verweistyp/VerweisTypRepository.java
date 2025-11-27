package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface VerweisTypRepository extends JpaRepository<VerweisTypEntity, UUID> {
  Page<VerweisTypEntity> findByNameContainingIgnoreCase(
    @Nonnull String name,
    @Nonnull Pageable pageable
  );

  Optional<VerweisTypEntity> findByTypNummer(@Nonnull String typNummer);
}
