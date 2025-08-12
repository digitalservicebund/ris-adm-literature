package de.bund.digitalservice.ris.adm_vwv.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.adm_vwv.application.converter.LdmlConverterService;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service for CRUD operations on document units.
 */
@Service
@RequiredArgsConstructor
public class DocumentationUnitService implements DocumentationUnitPort {

  private final DocumentationUnitPersistencePort documentationUnitPersistencePort;
  private final LdmlConverterService ldmlConverterService;
  private final ObjectMapper objectMapper;

  @Override
  public Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber) {
    var optionalDocumentationUnit = documentationUnitPersistencePort.findByDocumentNumber(
      documentNumber
    );
    if (
      optionalDocumentationUnit.isPresent() &&
      optionalDocumentationUnit.map(DocumentationUnit::json).isEmpty() &&
      optionalDocumentationUnit.map(DocumentationUnit::xml).isPresent()
    ) {
      // For an existing documentation unit without JSON but existing xml needs to be converted
      return convertLdml(optionalDocumentationUnit.get());
    }
    return optionalDocumentationUnit;
  }

  private Optional<DocumentationUnit> convertLdml(DocumentationUnit documentationUnit) {
    var documentationUnitContent = ldmlConverterService.convertToBusinessModel(documentationUnit);
    String json = convertToJson(documentationUnitContent);
    return Optional.of(new DocumentationUnit(documentationUnit, json));
  }

  private String convertToJson(DocumentationUnitContent documentationUnitContent) {
    try {
      return objectMapper.writeValueAsString(documentationUnitContent);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public DocumentationUnit create() {
    return documentationUnitPersistencePort.create();
  }

  @Override
  public Optional<DocumentationUnit> update(@Nonnull String documentNumber, @Nonnull String json) {
    return Optional.ofNullable(documentationUnitPersistencePort.update(documentNumber, json));
  }

  @Override
  public Optional<DocumentationUnit> publish(
    @Nonnull String documentNumber,
    @Nonnull DocumentationUnitContent documentationUnitContent
  ) {
    var optionalDocumentationUnit = documentationUnitPersistencePort.findByDocumentNumber(
      documentNumber
    );
    if (optionalDocumentationUnit.isPresent()) {
      DocumentationUnit documentationUnit = optionalDocumentationUnit.get();
      String xml = ldmlConverterService.convertToLdml(
        documentationUnitContent,
        documentationUnit.xml()
      );
      String json = convertToJson(documentationUnitContent);
      DocumentationUnit publishedDocumentationUnit = documentationUnitPersistencePort.publish(
        documentNumber,
        json,
        xml
      );
      return convertLdml(publishedDocumentationUnit);
    }
    return Optional.empty();
  }

  @Override
  public Page<DocumentationUnitOverviewElement> findDocumentationUnitOverviewElements(
    @Nonnull DocumentationUnitQuery queryOptions
  ) {
    return documentationUnitPersistencePort.findDocumentationUnitOverviewElements(queryOptions);
  }
}
