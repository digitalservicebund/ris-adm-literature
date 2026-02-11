package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaContextHolder;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitRepository;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.Fundstelle;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.Normgeber;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.LdmlToObjectConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.LiteratureDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

  private static final int INDEX_BATCH_SIZE = 500;
  private static final String SEPARATOR = " ";

  private final DocumentationUnitRepository documentationUnitRepository;
  private final DocumentationUnitIndexRepository documentationUnitIndexRepository;
  private final ObjectMapper objectMapper;
  private final LdmlToObjectConverterService ldmlToObjectConverterService;

  /**
   * Updates the index for the given documentation unit entity.
   *
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
    Class<? extends DocumentationUnitContent> documentationUnitContentClass =
      DocumentationUnitContent.getDocumentationUnitContentClass(
        documentationUnitEntity.getDocumentationUnitType()
      );
    if (documentationUnitEntity.getJson() == null && documentationUnitEntity.getXml() != null) {
      // Published documentation unit, there is only xml
      var documentationUnitContent = ldmlToObjectConverterService.convertToBusinessModel(
        new DocumentationUnit(
          documentationUnitEntity.getDocumentNumber(),
          documentationUnitEntity.getId(),
          null,
          documentationUnitEntity.getXml(),
          null
        ),
        documentationUnitContentClass
      );
      documentationUnitIndex = createDocumentationUnitIndex(
        documentationUnitEntity,
        documentationUnitContent
      );
    } else if (documentationUnitEntity.getJson() != null) {
      // Draft documentation unit, there is json
      DocumentationUnitContent documentationUnitContent = transformJson(
        documentationUnitEntity.getJson(),
        documentationUnitContentClass
      );
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
        AdmIndexData admIndexData = documentationUnitIndex.getAdmIndexData();
        admIndexData.setLangueberschrift(admDocumentationUnitContent.langueberschrift());
        if (admDocumentationUnitContent.fundstellen() != null) {
          var fundstellenList = admDocumentationUnitContent
            .fundstellen()
            .stream()
            .map(Fundstelle::toFormattedString)
            .toList();
          documentationUnitIndex.setFundstellen(fundstellenList);
          documentationUnitIndex.setFundstellenCombined(
            StringUtils.join(fundstellenList, SEPARATOR)
          );
        }
        admIndexData.setZitierdaten(admDocumentationUnitContent.zitierdaten());
        if (admDocumentationUnitContent.normgeberList() != null) {
          admIndexData.setNormgeberList(
            admDocumentationUnitContent.normgeberList().stream().map(Normgeber::format).toList()
          );
        }
        admIndexData.setAktenzeichenList(admDocumentationUnitContent.aktenzeichen());
        admIndexData.setInkrafttretedatum(admDocumentationUnitContent.inkrafttretedatum());
        if (admDocumentationUnitContent.dokumenttyp() != null) {
          admIndexData.setDokumenttyp(admDocumentationUnitContent.dokumenttyp().abbreviation());
        }
      }
      case LiteratureDocumentationUnitContent literatureDocumentationUnitContent -> {
        LiteratureIndexData literatureIndexData = documentationUnitIndex.getLiteratureIndexData();
        literatureIndexData.setTitel(literatureDocumentationUnitContent.titel());
        literatureIndexData.setVeroeffentlichungsjahr(
          literatureDocumentationUnitContent.veroeffentlichungsjahr()
        );
        if (literatureDocumentationUnitContent.dokumenttypen() != null) {
          literatureIndexData.setDokumenttypen(
            literatureDocumentationUnitContent
              .dokumenttypen()
              .stream()
              .map(DocumentType::abbreviation)
              .toList()
          );
        }
        literatureIndexData.setVerfasserList(null);
      }
      default -> log.debug(
        "Indexing document category {} is not supported.",
        documentationUnitContent.documentCategory()
      );
    }
    return documentationUnitIndex;
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
    documentationUnitIndex.getAdmIndexData().update(documentationUnitIndexEntity.getAdmIndex());
    documentationUnitIndex
      .getLiteratureIndexData()
      .update(documentationUnitIndexEntity.getLiteratureIndex());
    documentationUnitIndexEntity.setFundstellen(documentationUnitIndex.getFundstellen());
    documentationUnitIndexEntity.setFundstellenCombined(
      documentationUnitIndex.getFundstellenCombined()
    );
    return documentationUnitIndexEntity;
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
  @RequiredArgsConstructor
  private static class DocumentationUnitIndex {

    private final DocumentationUnitEntity documentationUnitEntity;
    private final AdmIndexData admIndexData = new AdmIndexData();
    private final LiteratureIndexData literatureIndexData = new LiteratureIndexData();
    private String fundstellenCombined;
    private List<String> fundstellen;
  }

  @Data
  private static class AdmIndexData {

    private String langueberschrift;
    private List<String> fundstellen;
    private List<String> zitierdaten;
    private String inkrafttretedatum;
    private List<String> normgeberList;
    private List<String> aktenzeichenList;
    private String dokumenttyp;

    void update(@Nonnull AdmIndex admIndex) {
      admIndex.setLangueberschrift(langueberschrift);
      admIndex.setZitierdaten(zitierdaten);
      admIndex.setZitierdatenCombined(StringUtils.join(zitierdaten, SEPARATOR));
      admIndex.setInkrafttretedatum(inkrafttretedatum);
      admIndex.setNormgeberList(normgeberList);
      admIndex.setNormgeberListCombined(StringUtils.join(normgeberList, SEPARATOR));
      admIndex.setAktenzeichenList(aktenzeichenList);
      admIndex.setAktenzeichenListCombined(StringUtils.join(aktenzeichenList, SEPARATOR));
      admIndex.setDokumenttyp(dokumenttyp);
    }
  }

  @Data
  private static class LiteratureIndexData {

    private String titel;
    private String veroeffentlichungsjahr;
    private List<String> dokumenttypen;
    private List<String> verfasserList;

    void update(@Nonnull LiteratureIndex literatureIndex) {
      literatureIndex.setTitel(titel);
      literatureIndex.setVeroeffentlichungsjahr(veroeffentlichungsjahr);
      literatureIndex.setDokumenttypen(dokumenttypen);
      literatureIndex.setDokumenttypenCombined(StringUtils.join(dokumenttypen, SEPARATOR));
      literatureIndex.setVerfasserList(verfasserList);
      literatureIndex.setVerfasserListCombined(StringUtils.join(verfasserList, SEPARATOR));
    }
  }
}
