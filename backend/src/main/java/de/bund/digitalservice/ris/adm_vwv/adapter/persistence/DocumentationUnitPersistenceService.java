package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.adm_vwv.application.*;
import de.bund.digitalservice.ris.adm_vwv.application.converter.LdmlConverterService;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence service for CRUD operations on documentation units
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentationUnitPersistenceService implements DocumentationUnitPersistencePort {

  private final DocumentationUnitCreationService documentationUnitCreationService;
  private final DocumentationUnitRepository documentationUnitRepository;
  private final DocumentationUnitIndexRepository documentationUnitIndexRepository;
  private final ObjectMapper objectMapper;
  private final LdmlConverterService ldmlConverterService;

  @Override
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

  @Override
  public DocumentationUnit create() {
    // Issue for the very first documentation unit of a new year: If for this year
    // there is no
    // document number stored, then concurrent threads can lead to an
    // DataIntegrityViolationException due to
    // unique constraint violation (try to persist same document number twice).
    // In that case the creation process is retried. Note, that even though the
    // exception is handled, there will
    // be a warn and an error message logged by Hibernate, which cannot be avoided.
    // The used retry template can only handle a database exception if the
    // underlying transaction completes
    // with a commit; therefore a secondary bean (DocumentationUnitCreationService)
    // is used to encapsulate
    // the transaction. This method must not have a @Transactional annotation.
    RetryTemplate retryTemplate = RetryTemplate.builder()
      .retryOn(DataIntegrityViolationException.class)
      .build();
    DocumentationUnit documentationUnit = retryTemplate.execute(_ ->
      documentationUnitCreationService.create()
    );
    log.info(
      "New documentation unit created with document number: {}",
      documentationUnit.documentNumber()
    );
    return documentationUnit;
  }

  @Override
  @Transactional
  public DocumentationUnit update(@Nonnull String documentNumber, @Nonnull String json) {
    return documentationUnitRepository
      .findByDocumentNumber(documentNumber)
      .map(documentationUnitEntity -> {
        documentationUnitEntity.setJson(json);
        return new DocumentationUnit(documentNumber, documentationUnitEntity.getId(), json);
      })
      .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DocumentationUnitOverviewElement> findDocumentationUnitOverviewElements(
    @Nonnull QueryOptions queryOptions
  ) {
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var documentationUnits = documentationUnitRepository.findByJsonIsNotNull(pageable);
    return PageTransformer.transform(documentationUnits, documentationUnitEntity -> {
      try {
        var documentationUnitContent = objectMapper.readValue(
          documentationUnitEntity.getJson(),
          DocumentationUnitContent.class
        );
        return new DocumentationUnitOverviewElement(
          documentationUnitEntity.getId(),
          documentationUnitEntity.getDocumentNumber(),
          documentationUnitContent.zitierdatum(),
          documentationUnitContent.langueberschrift(),
          mapFundstellen(documentationUnitContent)
        );
      } catch (JsonProcessingException e) {
        throw new IllegalStateException(
          "Exception during transforming document number: " +
          documentationUnitEntity.getDocumentNumber(),
          e
        );
      }
    });
  }

  private DocumentationUnitContent transformJson(
    @Nonnull DocumentationUnitEntity documentationUnitEntity
  ) {
    try {
      return objectMapper.readValue(
        documentationUnitEntity.getJson(),
        DocumentationUnitContent.class
      );
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(
        "Exception during transforming document number: " +
        documentationUnitEntity.getDocumentNumber(),
        e
      );
    }
  }

  private List<Fundstelle> mapFundstellen(DocumentationUnitContent documentationUnitContent) {
    if (documentationUnitContent.references() == null) {
      return List.of();
    }
    return documentationUnitContent
      .references()
      .stream()
      .map(reference ->
        new Fundstelle(
          reference.id(),
          reference.citation(),
          new Periodikum(
            reference.legalPeriodical().id(),
            reference.legalPeriodical().title(),
            reference.legalPeriodical().subtitle(),
            reference.legalPeriodical().abbreviation()
          )
        )
      )
      .toList();
  }

  /**
   * Executes indexing of the given documentation unit.
   *
   * @param documentationUnit The documentation unit to index
   */
  @Transactional
  public void index(@Nonnull DocumentationUnit documentationUnit) {
    if (documentationUnit.isEmpty()) {
      // No action needed. Content fields can never be set to null on update.
      return;
    }
    var documentationUnitIndexEntity = documentationUnitIndexRepository
      .findByDocumentationUnitId(documentationUnit.id())
      .orElseGet(() -> {
        var documentationUnitIndexEntityNew = new DocumentationUnitIndexEntity();
        var documentationUnitEntity = documentationUnitRepository.getReferenceById(
          documentationUnit.id()
        );
        documentationUnitIndexEntityNew.setDocumentationUnit(documentationUnitEntity);
        return documentationUnitIndexEntityNew;
      });
    if (documentationUnit.json() == null && documentationUnit.xml() != null) {
      // Published documentation unit, there is only xml
      var documentationUnitContent = ldmlConverterService.convertToBusinessModel(documentationUnit);
      updateDocumentationUnitIndexEntity(documentationUnitIndexEntity, documentationUnitContent);
    } else if (documentationUnit.json() != null) {
      // Draft documentation unit, there is json
      DocumentationUnitContent documentationUnitContent = transformJson(
        documentationUnitIndexEntity.getDocumentationUnit()
      );
      updateDocumentationUnitIndexEntity(documentationUnitIndexEntity, documentationUnitContent);
    }
    documentationUnitIndexRepository.save(documentationUnitIndexEntity);
  }

  private void updateDocumentationUnitIndexEntity(
    DocumentationUnitIndexEntity documentationUnitIndexEntity,
    DocumentationUnitContent documentationUnitContent
  ) {
    documentationUnitIndexEntity.setLangueberschrift(documentationUnitContent.langueberschrift());
    documentationUnitIndexEntity.setFundstellen(
      documentationUnitContent
        .references()
        .stream()
        .map(r -> r.legalPeriodicalRawValue() + " " + r.citation())
        .collect(Collectors.joining(" "))
    );
    documentationUnitIndexEntity.setZitierdaten(
      String.join(" ", documentationUnitContent.zitierdaten())
    );
  }
}
