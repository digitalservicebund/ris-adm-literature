package de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for document type lookup table.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentTypeService {

  private final DocumentTypeRepository documentTypeRepository;

  /**
   * Finds a paginated list of document types based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link DocumentType}.
   */
  @Transactional(readOnly = true)
  public Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    DocumentTypeEntity probe = new DocumentTypeEntity();
    probe.setDocumentCategory(query.documentCategory());
    Example<DocumentTypeEntity> example = Example.of(probe);
    var documentTypes = StringUtils.isBlank(searchTerm)
      ? documentTypeRepository.findAll(example, pageable)
      : documentTypeRepository.findByDocumentCategoryAndAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
        query.documentCategory(),
        searchTerm,
        searchTerm,
        pageable
      );
    return PageTransformer.transform(documentTypes, mapDocumentTypeEntity());
  }

  /**
   * Finds a single document type by its abbreviation.
   *
   * @param abbreviation The abbreviation of the document type to find.
   * @param documentCategory The documentation category of the document type to find
   * @return An {@link Optional} containing the found {@link DocumentType}, or empty if not found.
   */
  @Transactional(readOnly = true)
  public Optional<DocumentType> findDocumentTypeByAbbreviation(
    @Nonnull String abbreviation,
    @Nonnull DocumentCategory documentCategory
  ) {
    return documentTypeRepository
      .findByAbbreviationAndDocumentCategory(abbreviation, documentCategory)
      .map(mapDocumentTypeEntity());
  }

  private Function<DocumentTypeEntity, DocumentType> mapDocumentTypeEntity() {
    return documentTypeEntity ->
      new DocumentType(documentTypeEntity.getAbbreviation(), documentTypeEntity.getName());
  }
}
