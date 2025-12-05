package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.DocumentationUnitIndexService.ENTRY_SEPARATOR;

import de.bund.digitalservice.ris.adm_literature.config.security.UserDocumentDetails;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentionUnitSpecification;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.DocumentationUnitIndexEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.DocumentationUnitIndexService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.LiteratureDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.LiteratureDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitSpecification;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentTypeService;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence service for CRUD operations on documentation units
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentationUnitPersistenceService {

  private final DocumentationUnitCreationService documentationUnitCreationService;
  private final DocumentationUnitIndexService documentationUnitIndexService;
  private final DocumentationUnitRepository documentationUnitRepository;
  private final DocumentTypeService documentTypeService;

  /**
   * Finds a document by its number.
   *
   * @param documentNumber The document number
   * @return an {@link Optional} containing the found {@link DocumentationUnit}, or empty if not found.
   */
  @Transactional(readOnly = true)
  public Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber) {
    return documentationUnitRepository
      .findByDocumentNumber(documentNumber)
      .map(documentationUnitEntity ->
        new DocumentationUnit(
          documentNumber,
          documentationUnitEntity.getId(),
          documentationUnitEntity.getJson(),
          documentationUnitEntity.getXml()
        )
      );
  }

  /**
   * Creates a new documentation unit based on the authenticated user's details.
   *
   * @param documentCategory The document category of the documentation unit
   * @return The newly created and persisted {@link DocumentationUnit}.
   */
  @Retryable(DataIntegrityViolationException.class)
  public DocumentationUnit create(DocumentCategory documentCategory) {
    // Issue for the very first documentation unit of a new year: If for this year
    // there is no
    // document number stored, then concurrent threads can lead to an
    // DataIntegrityViolationException due to
    // unique constraint violation (try to persist same document number twice).
    // In that case the creation process is retried. Note, that even though the
    // exception is handled, there will
    // be a warning and an error message logged by Hibernate, which cannot be avoided.
    // The used retry template can only handle a database exception if the
    // underlying transaction completes
    // with a commit; therefore a secondary bean (DocumentationUnitCreationService)
    // is used to encapsulate
    // the transaction. This method must not have a @Transactional annotation.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDocumentDetails details = (UserDocumentDetails) authentication.getPrincipal();
    DocumentationUnit documentationUnit = documentationUnitCreationService.create(
      details.office(),
      documentCategory
    );
    log.info(
      "New documentation unit created with document number: {}",
      documentationUnit.documentNumber()
    );
    return documentationUnit;
  }

  /**
   * Updates a documentation unit by document number and returns the updated documentation unit.
   *
   * @param documentNumber The document number to identify the documentation unit
   * @param json           The json string to update
   * @return The updated documentation unit or an empty optional, if there is no documentation unit
   *         with the given document number
   */
  @Transactional
  public DocumentationUnit update(@Nonnull String documentNumber, @Nonnull String json) {
    return documentationUnitRepository
      .findByDocumentNumber(documentNumber)
      .map(documentationUnitEntity -> {
        documentationUnitEntity.setJson(json);
        log.info("Updated documentation unit with document number: {}.", documentNumber);
        documentationUnitIndexService.updateIndex(documentationUnitEntity);
        return new DocumentationUnit(documentNumber, documentationUnitEntity.getId(), json);
      })
      .orElse(null);
  }

  /**
   * Updates the content of a specific documentation unit and regenerates its search index.
   * <p>
   * This entire operation is performed within a single database transaction. If the unit is not found,
   * this method returns {@code null}.
   *
   * @param documentNumber The unique identifier of the documentation unit to update.
   * @param json           The new JSON content for the unit.
   * @param xml            The new XML content for the unit.
   * @return The updated {@link DocumentationUnit}, or {@code null} if the document number does not exist.
   */
  @Transactional
  public DocumentationUnit publish(
    @Nonnull String documentNumber,
    @Nonnull String json,
    @Nonnull String xml
  ) {
    return documentationUnitRepository
      .findByDocumentNumber(documentNumber)
      .map(documentationUnitEntity -> {
        documentationUnitEntity.setJson(json);
        documentationUnitEntity.setXml(xml);
        log.info("Published documentation unit with document number: {}.", documentNumber);
        documentationUnitIndexService.updateIndex(documentationUnitEntity);
        return new DocumentationUnit(documentNumber, documentationUnitEntity.getId(), null, xml);
      })
      .orElse(null);
  }

  /**
   * Returns paginated documentation units overview elements.
   *
   * @param query The query
   * @return Page object with documentation unit overview elements and pagination data
   */
  @Transactional(readOnly = true)
  public Page<AdmDocumentationUnitOverviewElement> findAdmDocumentationUnitOverviewElements(
    @Nonnull AdmDocumentationUnitQuery query
  ) {
    QueryOptions queryOptions = query.queryOptions();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    AdmDocumentionUnitSpecification admDocumentionUnitSpecification =
      new AdmDocumentionUnitSpecification(
        query.documentNumber(),
        query.langueberschrift(),
        query.fundstellen(),
        query.zitierdaten()
      );
    var documentationUnitsPage = documentationUnitRepository.findAll(
      admDocumentionUnitSpecification,
      pageable
    );
    return PageTransformer.transform(documentationUnitsPage, documentationUnit -> {
      DocumentationUnitIndexEntity index = documentationUnit.getDocumentationUnitIndex();

      if (index == null) {
        return new AdmDocumentationUnitOverviewElement(
          documentationUnit.getId(),
          documentationUnit.getDocumentNumber(),
          Collections.emptyList(),
          null,
          Collections.emptyList()
        );
      }

      return new AdmDocumentationUnitOverviewElement(
        documentationUnit.getId(),
        documentationUnit.getDocumentNumber(),
        splitBySeparator(index.getZitierdaten()),
        index.getLangueberschrift(),
        splitBySeparator(index.getFundstellen())
      );
    });
  }

  /**
   * Returns paginated documentation units overview elements.
   *
   * @param query The query
   * @return Page object with documentation unit overview elements and pagination data
   */
  @Transactional(readOnly = true)
  public Page<
    LiteratureDocumentationUnitOverviewElement
  > findLiteratureDocumentationUnitOverviewElements(
    @Nonnull LiteratureDocumentationUnitQuery query
  ) {
    QueryOptions queryOptions = query.queryOptions();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    SliDocumentationUnitSpecification documentUnitSpecification =
      new SliDocumentationUnitSpecification(
        query.documentNumber(),
        query.veroeffentlichungsjahr(),
        query.dokumenttypen(),
        query.titel(),
        query.verfasser()
      );
    var documentationUnitsPage = documentationUnitRepository.findAll(
      documentUnitSpecification,
      pageable
    );
    final Map<String, String> typeLookup = documentTypeService.getDocumentTypeNames();

    return PageTransformer.transform(documentationUnitsPage, documentationUnit -> {
      DocumentationUnitIndexEntity index = documentationUnit.getDocumentationUnitIndex();

      if (index == null) {
        return new LiteratureDocumentationUnitOverviewElement(
          documentationUnit.getId(),
          documentationUnit.getDocumentNumber(),
          null,
          null,
          Collections.emptyList(),
          Collections.emptyList()
        );
      }

      List<String> documentTypeNames = splitBySeparator(index.getDokumenttypen())
        .stream()
        .map(abbrev -> typeLookup.getOrDefault(abbrev, abbrev))
        .toList();

      return new LiteratureDocumentationUnitOverviewElement(
        documentationUnit.getId(),
        documentationUnit.getDocumentNumber(),
        index.getVeroeffentlichungsjahr(),
        index.getTitel(),
        documentTypeNames,
        splitBySeparator(index.getVerfasser())
      );
    });
  }

  private List<String> splitBySeparator(String value) {
    String[] separatedValues = StringUtils.splitByWholeSeparator(value, ENTRY_SEPARATOR);
    return separatedValues != null ? List.of(separatedValues) : List.of();
  }
}
