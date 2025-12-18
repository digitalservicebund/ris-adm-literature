package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaContextHolder;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaRoutingDataSource;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import de.bund.digitalservice.ris.adm_literature.config.security.UserDocumentDetails;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentionUnitSpecification;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.AdmIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.DocumentationUnitIndexEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.DocumentationUnitIndexService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.LiteratureIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.notes.NoteService;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

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
  private final NoteService noteService;
  private final ObjectMapper objectMapper;

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
      .map(documentationUnitEntity -> {
        String json = documentationUnitEntity.getJson();
        String note = noteService.find(documentationUnitEntity);
        if (ObjectUtils.allNotNull(json, note)) {
          JsonNode jsonNode = objectMapper.readTree(json);
          ((ObjectNode) jsonNode).put("note", note);
          json = objectMapper.writeValueAsString(jsonNode);
        }
        return new DocumentationUnit(
          documentNumber,
          documentationUnitEntity.getId(),
          json,
          documentationUnitEntity.getXml(),
          note
        );
      });
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
        String note = objectMapper.readTree(json).path("note").asString(null);
        noteService.save(documentationUnitEntity, note);
        documentationUnitIndexService.updateIndex(documentationUnitEntity);
        return new DocumentationUnit(
          documentNumber,
          documentationUnitEntity.getId(),
          json,
          null,
          note
        );
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
        String note = objectMapper.readTree(json).path("note").asString(null);
        noteService.save(documentationUnitEntity, note);
        log.info("Published documentation unit with document number: {}.", documentNumber);
        documentationUnitIndexService.updateIndex(documentationUnitEntity);
        return new DocumentationUnit(
          documentNumber,
          documentationUnitEntity.getId(),
          json,
          xml,
          note
        );
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
      AdmIndex admIndex = index.getAdmIndex();
      return new AdmDocumentationUnitOverviewElement(
        documentationUnit.getId(),
        documentationUnit.getDocumentNumber(),
        admIndex.getZitierdaten(),
        admIndex.getLangueberschrift(),
        admIndex.getFundstellen()
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

      LiteratureIndex literatureIndex = index.getLiteratureIndex();
      return new LiteratureDocumentationUnitOverviewElement(
        documentationUnit.getId(),
        documentationUnit.getDocumentNumber(),
        literatureIndex.getVeroeffentlichungsjahr(),
        literatureIndex.getTitel(),
        literatureIndex.getDokumenttypen(),
        literatureIndex.getVerfasserList()
      );
    });
  }

  /**
   * Searches for administrative citations (Aktivzitierungen) across documentation units.
   * * <p>This method uses {@code Propagation.REQUIRES_NEW} to force the creation of a new
   * database connection. This is critical in our multi-schema setup to ensure the
   * {@link SchemaRoutingDataSource} performs a fresh lookup of the current
   * {@link SchemaContextHolder} value, allowing the query to target the ADM schema
   * even if called from within a LIT schema request context.</p>
   *
   * @param query The search criteria including document metadata and pagination options.
   * @return A paginated list of ADM overview elements transformed from the persistence layer.
   */
  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  public Page<AdmAktivzitierungOverviewElement> findAktivzitierungen(
    @Nonnull AktivzitierungQuery query
  ) {
    QueryOptions queryOptions = query.queryOptions();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    SchemaType currentSchema = SchemaContextHolder.getSchema();
    log.info("Current application schema context: {}", currentSchema);
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);

    AktivzitierungAdmSpecification spec = new AktivzitierungAdmSpecification(
      query.documentNumber(),
      query.periodikum(),
      query.zitatstelle(),
      query.inkrafttretedatum(),
      query.aktenzeichen(),
      query.dokumenttyp(),
      query.normgeber()
    );

    var documentationUnitsPage = documentationUnitRepository.findAll(spec, pageable);

    return PageTransformer.transform(documentationUnitsPage, documentationUnit -> {
      DocumentationUnitIndexEntity index = documentationUnit.getDocumentationUnitIndex();

      if (index == null) {
        return new AdmAktivzitierungOverviewElement(
          documentationUnit.getId(),
          documentationUnit.getDocumentNumber(),
          null,
          null,
          null,
          null,
          null,
          null,
          null
        );
      }

      var admIndex = index.getAdmIndex();
      return new AdmAktivzitierungOverviewElement(
        documentationUnit.getId(),
        documentationUnit.getDocumentNumber(),
        admIndex.getInkrafttretedatum(),
        admIndex.getLangueberschrift(),
        admIndex.getDokumenttyp(),
        admIndex.getNormgeberListCombined(),
        admIndex.getFundstellenCombined(),
        admIndex.getZitierdatenCombined(),
        admIndex.getAktenzeichenListCombined()
      );
    });
  }
}
