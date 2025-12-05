package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaContextHolder;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.LdmlConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.LiteratureDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Documentation unit index service for building an index for documentation units.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentationUnitIndexService {

  public static final String ENTRY_SEPARATOR = "$µµµµµ$";
  private static final int INDEX_BATCH_SIZE = 500;

  private final DocumentationUnitRepository documentationUnitRepository;
  private final DocumentationUnitIndexRepository documentationUnitIndexRepository;
  private final ObjectMapper objectMapper;
  private final LdmlConverterService ldmlConverterService;

  /**
   * Updates the index for the given documentation unit entity.
   * @param documentationUnitEntity The documentation unit entity for which the index is to be updated
   */
  @Transactional(propagation = Propagation.MANDATORY)
  public void updateIndex(DocumentationUnitEntity documentationUnitEntity) {
    documentationUnitIndexRepository.save(
      mapDocumentationUnitIndex(createIndex(documentationUnitEntity))
    );
    log.info(
      "Re-indexed documentation unit with document number: {}.",
      documentationUnitEntity.getDocumentNumber()
    );
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
   * @param schemaType The schema to use
   * @return Number of indexed documents
   */
  public long updateIndex(@NonNull SchemaType schemaType) {
    SchemaContextHolder.setSchema(schemaType);
    log.info(
      "Found {} documentation units without index.",
      documentationUnitRepository.countByDocumentationUnitIndexIsNull()
    );
    PageRequest pageable = PageRequest.of(0, INDEX_BATCH_SIZE);
    long totalNumberOfElements = 0;
    int pageNumber = 0;
    Slice<DocumentationUnitEntity> documentationUnitEntities;
    do {
      // Note that we always select the first page (0) because the algorithm is changing the selection result
      // and breaks linear paging.
      documentationUnitEntities =
        documentationUnitRepository.findByDocumentationUnitIndexIsNullOrderByDocumentNumberDesc(
          pageable
        );
      List<DocumentationUnitIndexEntity> documentationUnitIndexEntities = documentationUnitEntities
        .stream()
        .unordered()
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
      DocumentationUnitContent documentationUnitContent =
        switch (documentationUnitEntity.getDocumentationUnitType()) {
          case VERWALTUNGSVORSCHRIFTEN -> transformJson(
            documentationUnitEntity.getJson(),
            AdmDocumentationUnitContent.class
          );
          case LITERATUR_SELBSTAENDIG -> transformJson(
            documentationUnitEntity.getJson(),
            SliDocumentationUnitContent.class
          );
          case LITERATUR_UNSELBSTAENDIG -> transformJson(
            documentationUnitEntity.getJson(),
            UliDocumentationUnitContent.class
          );
        };
      documentationUnitIndex = createDocumentationUnitIndex(
        documentationUnitEntity,
        documentationUnitContent
      );
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
      case LiteratureDocumentationUnitContent literatureDocumentationUnitContent -> {
        documentationUnitIndex.setTitel(literatureDocumentationUnitContent.titel());
        documentationUnitIndex.setVeroeffentlichungsjahr(
          literatureDocumentationUnitContent.veroeffentlichungsjahr()
        );
        if (literatureDocumentationUnitContent.dokumenttypen() != null) {
          documentationUnitIndex.setDokumenttypen(
            literatureDocumentationUnitContent
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
