package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import java.time.Year;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

interface DocumentNumberRepository extends JpaRepository<DocumentNumberEntity, UUID> {
  /**
   * Finds the current document number by the given year. The operation creates a pessimistic lock for the found
   * entity, so that concurrent threads have to wait until the transaction completes.
   * @param year The year the document number belongs to
   * @return Document number with the given year or an empty optional if not found
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<DocumentNumberEntity> findByPrefixAndYear(@Nonnull String prefix, @Nonnull Year year);
}
