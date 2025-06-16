package de.bund.digitalservice.ris.adm_vwv.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.adm_vwv.application.converter.LdmlConverterService;
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
      DocumentationUnit documentationUnit = optionalDocumentationUnit.get();
      var documentationUnitContent = ldmlConverterService.convertToBusinessModel(documentationUnit);
      try {
        return Optional.of(
          new DocumentationUnit(
            documentationUnit,
            objectMapper.writeValueAsString(documentationUnitContent)
          )
        );
      } catch (JsonProcessingException e) {
        throw new IllegalStateException(e);
      }
    }
    return optionalDocumentationUnit;
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
  public Page<DocumentationUnitOverviewElement> findDocumentationUnitOverviewElements(
    @Nonnull DocumentationUnitQuery queryOptions
  ) {
    return documentationUnitPersistencePort.findDocumentationUnitOverviewElements(queryOptions);
  }
}
