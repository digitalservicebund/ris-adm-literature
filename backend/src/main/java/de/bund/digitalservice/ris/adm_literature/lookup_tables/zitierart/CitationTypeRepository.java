package de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import jakarta.annotation.Nonnull;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CitationTypeRepository extends JpaRepository<CitationTypeEntity, UUID> {
  @Query(
    """
    select ct from CitationTypeEntity ct where ct.sourceDocumentCategory = :sourceDocumentCategory \
    and ct.targetDocumentCategory = :targetDocumentCategory \
    and (upper(ct.abbreviation) like (upper(concat('%', :abbreviation, '%'))) \
    or (upper(ct.label) like (upper(concat('%', :label, '%')))))"""
  )
  Page<CitationTypeEntity> findBySourceAndTargetAndAbbreviationOrLabel(
    @Nonnull @Param("sourceDocumentCategory") DocumentCategory sourceDocumentCategory,
    @Param("targetDocumentCategory") DocumentCategory targetDocumentCategory,
    @Nonnull @Param("abbreviation") String abbreviation,
    @Nonnull @Param("label") String label,
    @Nonnull Pageable pageable
  );
}
