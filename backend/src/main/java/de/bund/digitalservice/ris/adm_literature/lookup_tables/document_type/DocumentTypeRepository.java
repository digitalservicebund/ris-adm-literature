package de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentTypeEntity, UUID> {
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

  /**
   * Retrieves a list of DocumentTypeEntity instances that match the given document category
   * and have abbreviations contained in the specified collection.
   *
   * @param documentCategory the category of the documents to filter by; must not be null
   * @param abbreviations the collection of abbreviations to filter by; must not be null
   * @return a list of matching DocumentTypeEntity instances based on the provided criteria
   */
  List<DocumentTypeEntity> findByDocumentCategoryAndAbbreviationIn(
    @Nonnull DocumentCategory documentCategory,
    @Nonnull Collection<String> abbreviations
  );
}
