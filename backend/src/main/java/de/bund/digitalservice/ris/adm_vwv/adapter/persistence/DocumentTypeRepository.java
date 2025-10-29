package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentTypeRepository extends JpaRepository<DocumentTypeEntity, UUID> {
  Page<
    DocumentTypeEntity
  > findByDocumentCategoryAndAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
    @Nonnull DocumentCategory documentCategory,
    @Nonnull String abbreviation,
    @Nonnull String name,
    @Nonnull Pageable pageable
  );

  Optional<DocumentTypeEntity> findByAbbreviationAndDocumentCategory(
    String abbreviation,
    DocumentCategory documentCategory
  );
}
