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
  Optional<DocumentNumberEntity> findByYear(@Nonnull Year year);

  /**
   * Finds a document number entity by its year and specific documentation office.
   * This is the primary method for finding the correct counter for a given office.
   *
   * @param year The calendar year.
   * @param documentationOffice The office (e.g., BAG, BFH) whose counter we need.
   * @return An optional containing the document number entity if found.
   */
  Optional<DocumentNumberEntity> findByYearAndDocumentationOffice(
    Year year,
    DocumentationOffice documentationOffice
  );
}
