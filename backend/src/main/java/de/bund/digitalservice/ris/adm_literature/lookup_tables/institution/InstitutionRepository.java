package de.bund.digitalservice.ris.adm_literature.lookup_tables.institution;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface InstitutionRepository extends JpaRepository<InstitutionEntity, UUID> {
  Page<InstitutionEntity> findByNameContainingIgnoreCase(
    @Nonnull String name,
    @Nonnull Pageable pageable
  );
  Optional<InstitutionEntity> findByNameAndType(@Nonnull String name, @Nonnull String type);
}
