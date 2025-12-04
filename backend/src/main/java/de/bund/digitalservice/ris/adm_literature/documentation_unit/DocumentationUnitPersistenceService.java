package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.config.security.UserDocumentDetails;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.LdmlConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentTypeService;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Persistence service for CRUD operations on documentation units
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentationUnitPersistenceService {

  public static final String ENTRY_SEPARATOR = "$µµµµµ$";

  private static final int INDEX_BATCH_SIZE = 500;

  private final DocumentationUnitCreationService documentationUnitCreationService;
  private final DocumentationUnitRepository documentationUnitRepository;
  private final DocumentationUnitIndexRepository documentationUnitIndexRepository;
  private final DocumentTypeService documentTypeService;
  private final ObjectMapper objectMapper;
  private final LdmlConverterService ldmlConverterService;

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
        documentationUnitIndexRepository.save(
          mapDocumentationUnitIndex(createIndex(documentationUnitEntity))
        );
        log.info("Re-indexed documentation unit with document number: {}.", documentNumber);
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
        documentationUnitIndexRepository.save(
          mapDocumentationUnitIndex(createIndex(documentationUnitEntity))
        );
        log.info("Re-indexed documentation unit with document number: {}.", documentNumber);
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

  /**
   * Execute indexing of all documentation units without documentation unit index.
   * <p>
   * The method selects all documentation units which are unindexed in batches and extracts
   * the needed data for the index in parallel. Exceptions during the extraction
   * are ignored. After extraction a new instance of {@link DocumentationUnitIndexEntity} is
   * created and saved.
   * </p>
   * <p>
   * <b>NOTE:</b>This method guarantees that an index is created for each documentation unit
   * without an index before calling this method.
   * </p>
   *
   * @return Number of indexed documents
   */
  public long indexAll() {
    PageRequest pageable = PageRequest.of(0, INDEX_BATCH_SIZE);
    long totalNumberOfElements = 0;
    int pageNumber = 0;
    Slice<DocumentationUnitEntity> documentationUnitEntities;
    log.info(
      "Found {} documentation units without index.",
      documentationUnitRepository.countByDocumentationUnitIndexIsNull()
    );
    do {
      // Note that we always select the first page (0) because the algorithm is changing the selection result
      // and breaks linear paging.
      documentationUnitEntities =
        documentationUnitRepository.findByDocumentationUnitIndexIsNullOrderByDocumentNumberDesc(
          pageable
        );
      List<DocumentationUnitIndexEntity> documentationUnitIndexEntities = documentationUnitEntities
        .stream()
        .parallel()
        .map(this::createIndexSafely)
        .map(this::mapDocumentationUnitIndex)
        .toList();
      documentationUnitIndexRepository.saveAll(documentationUnitIndexEntities);
      totalNumberOfElements += documentationUnitEntities.getNumberOfElements();
      log.info(
        "Indexing {} documentation units, page {}. Sum: {}.",
        documentationUnitEntities.getNumberOfElements(),
        pageNumber++,
        totalNumberOfElements
      );
    } while (documentationUnitEntities.hasNext());
    return totalNumberOfElements;
  }

  private DocumentationUnitIndexEntity mapDocumentationUnitIndex(
    DocumentationUnitIndex documentationUnitIndex
  ) {
    DocumentationUnitIndexEntity documentationUnitIndexEntity =
      documentationUnitIndex.documentationUnitEntity.getDocumentationUnitIndex();
    if (documentationUnitIndexEntity == null) {
      // New entry for created or imported documents
      documentationUnitIndexEntity = new DocumentationUnitIndexEntity();
      documentationUnitIndexEntity.setDocumentationUnit(
        documentationUnitIndex.documentationUnitEntity
      );
    }
    documentationUnitIndexEntity.setLangueberschrift(documentationUnitIndex.getLangueberschrift());
    documentationUnitIndexEntity.setFundstellen(documentationUnitIndex.getFundstellen());
    documentationUnitIndexEntity.setZitierdaten(documentationUnitIndex.getZitierdaten());
    documentationUnitIndexEntity.setTitel(documentationUnitIndex.getTitel());
    documentationUnitIndexEntity.setVeroeffentlichungsjahr(
      documentationUnitIndex.getVeroeffentlichungsjahr()
    );
    documentationUnitIndexEntity.setDokumenttypen(documentationUnitIndex.getDokumenttypen());
    documentationUnitIndexEntity.setVerfasser(documentationUnitIndex.getVerfasser());
    return documentationUnitIndexEntity;
  }

  private DocumentationUnitIndex createIndexSafely(
    DocumentationUnitEntity documentationUnitEntity
  ) {
    try {
      return createIndex(documentationUnitEntity);
    } catch (Exception e) {
      log.warn(
        "Could not index documentation unit {}. Reason: {}.",
        documentationUnitEntity.getDocumentNumber(),
        e.getMessage()
      );
      log.debug("Stacktrace:", e);
    }
    // We save an empty entry so the document still appears on overview page
    // Content fields (langueberschrift, etc.) remain null as intended on error
    return new DocumentationUnitIndex(documentationUnitEntity);
  }

  private DocumentationUnitIndex createIndex(
    @Nonnull DocumentationUnitEntity documentationUnitEntity
  ) {
    DocumentationUnitIndex documentationUnitIndex = new DocumentationUnitIndex(
      documentationUnitEntity
    );
    if (documentationUnitEntity.isEmpty()) {
      // We save an empty entry so the document still appears on overview page
      return documentationUnitIndex;
    }
    if (documentationUnitEntity.getJson() == null && documentationUnitEntity.getXml() != null) {
      // Published documentation unit, there is only xml
      var documentationUnitContent = ldmlConverterService.convertToBusinessModel(
        new DocumentationUnit(
          documentationUnitEntity.getDocumentNumber(),
          documentationUnitEntity.getId(),
          null,
          documentationUnitEntity.getXml()
        )
      );
      documentationUnitIndex = createDocumentationUnitIndex(
        documentationUnitEntity,
        documentationUnitContent
      );
    } else if (documentationUnitEntity.getJson() != null) {
      // Draft documentation unit, there is json
      switch (documentationUnitEntity.getDocumentationUnitType()) {
        case VERWALTUNGSVORSCHRIFTEN -> {
          AdmDocumentationUnitContent admDocumentationUnitContent = transformJson(
            documentationUnitEntity.getJson(),
            AdmDocumentationUnitContent.class
          );
          documentationUnitIndex = createDocumentationUnitIndex(
            documentationUnitEntity,
            admDocumentationUnitContent
          );
        }
        case LITERATUR_SELBSTAENDIG -> {
          SliDocumentationUnitContent sliDocumentationUnitContent = transformJson(
            documentationUnitEntity.getJson(),
            SliDocumentationUnitContent.class
          );
          documentationUnitIndex = createDocumentationUnitIndex(
            documentationUnitEntity,
            sliDocumentationUnitContent
          );
        }
        default -> log.warn(
          "Indexing document category {} is not supported.",
          documentationUnitEntity.getDocumentationUnitType()
        );
      }
    }
    return documentationUnitIndex;
  }

  private DocumentationUnitIndex createDocumentationUnitIndex(
    DocumentationUnitEntity documentationUnitEntity,
    DocumentationUnitContent documentationUnitContent
  ) {
    DocumentationUnitIndex documentationUnitIndex = new DocumentationUnitIndex(
      documentationUnitEntity
    );
    switch (documentationUnitContent) {
      case AdmDocumentationUnitContent admDocumentationUnitContent -> {
        documentationUnitIndex.setLangueberschrift(admDocumentationUnitContent.langueberschrift());
        if (admDocumentationUnitContent.fundstellen() != null) {
          documentationUnitIndex.setFundstellen(
            admDocumentationUnitContent
              .fundstellen()
              .stream()
              .map(
                f ->
                  (f.ambiguousPeriodikum() != null
                      ? f.ambiguousPeriodikum()
                      : f.periodikum().abbreviation()) +
                  " " +
                  f.zitatstelle()
              )
              .collect(Collectors.joining(ENTRY_SEPARATOR))
          );
        }
        if (admDocumentationUnitContent.zitierdaten() != null) {
          documentationUnitIndex.setZitierdaten(
            String.join(ENTRY_SEPARATOR, admDocumentationUnitContent.zitierdaten())
          );
        }
      }
      case SliDocumentationUnitContent sliDocumentationUnitContent -> {
        documentationUnitIndex.setTitel(sliDocumentationUnitContent.titel());
        documentationUnitIndex.setVeroeffentlichungsjahr(
          sliDocumentationUnitContent.veroeffentlichungsjahr()
        );
        if (sliDocumentationUnitContent.dokumenttypen() != null) {
          documentationUnitIndex.setDokumenttypen(
            sliDocumentationUnitContent
              .dokumenttypen()
              .stream()
              .map(DocumentType::abbreviation)
              .collect(Collectors.joining(ENTRY_SEPARATOR))
          );
        }
        documentationUnitIndex.setVerfasser(null);
      }
      default -> log.debug(
        "Indexing document category {} is not supported.",
        documentationUnitContent.documentCategory()
      );
    }
    return documentationUnitIndex;
  }

  private <T extends DocumentationUnitContent> T transformJson(
    @Nonnull String json,
    Class<T> clazz
  ) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JacksonException e) {
      throw new IllegalStateException("Exception during transforming json: " + json, e);
    }
  }

  @Data
  @AllArgsConstructor
  @RequiredArgsConstructor
  private static class DocumentationUnitIndex {

    private final DocumentationUnitEntity documentationUnitEntity;
    private String langueberschrift;
    private String fundstellen;
    private String zitierdaten;
    private String titel;
    private String veroeffentlichungsjahr;
    private String dokumenttypen;
    private String verfasser;
  }
}
