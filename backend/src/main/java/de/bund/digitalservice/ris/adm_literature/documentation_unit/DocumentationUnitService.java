package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaExecutor;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import de.bund.digitalservice.ris.adm_literature.config.security.UserDocumentDetails;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.LdmlToObjectConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ObjectToLdmlConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.Publisher;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Application service for CRUD operations on document units.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentationUnitService {

  private final DocumentationUnitPersistenceService documentationUnitPersistenceService;
  private final LdmlToObjectConverterService ldmlToObjectConverterService;
  private final ObjectToLdmlConverterService objectToLdmlConverterService;
  private final ObjectMapper objectMapper;
  private final Publisher publisher;
  private final SchemaExecutor schemaExecutor;

  /**
   * Finds a DocumentationUnit by its document number.
   * If the found unit exists with XML but no JSON, it is converted before being returned.
   *
   * @param documentNumber The document number to search for.
   * @return An {@link Optional} containing the DocumentationUnit, or an empty Optional if not found.
   */
  public Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber) {
    var optionalDocumentationUnit = documentationUnitPersistenceService.findByDocumentNumber(
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
    var documentationUnitContent = ldmlToObjectConverterService.convertToBusinessModel(
      documentationUnit,
      AdmDocumentationUnitContent.class
    );
    String json = convertToJson(documentationUnitContent);
    return Optional.of(new DocumentationUnit(documentationUnit, json));
  }

  private String convertToJson(DocumentationUnitContent documentationUnitContent) {
    try {
      return objectMapper.writeValueAsString(documentationUnitContent);
    } catch (JacksonException e) {
      throw new IllegalStateException(e);
    }
  }

  public DocumentationUnit create(@Nonnull DocumentCategory documentCategory) {
    return documentationUnitPersistenceService.create(documentCategory);
  }

  public Optional<DocumentationUnit> update(@Nonnull String documentNumber, @Nonnull String json) {
    return Optional.ofNullable(documentationUnitPersistenceService.update(documentNumber, json));
  }

  /**
   * Updates and publishes a DocumentationUnit with new content.
   * <p>
   * The update is persisted to the database and then published to an external bucket.
   * The {@link UserDocumentDetails} decide to which bucket to publish (to be implemented):
   * {@code UserDocumentDetails details = (UserDocumentDetails) authentication.getPrincipal()}
   * This entire operation is transactional and will be rolled back if any step fails.
   *
   * @param documentNumber The identifier of the documentation unit to publish.
   * @param documentationUnitContent The new content for the unit.
   * @return An {@link Optional} with the updated unit, or empty if the document number was not found.
   */
  @Transactional
  public Optional<DocumentationUnit> publish(
    @Nonnull String documentNumber,
    @Nonnull DocumentationUnitContent documentationUnitContent
  ) {
    var optionalDocumentationUnit = documentationUnitPersistenceService.findByDocumentNumber(
      documentNumber
    );

    if (optionalDocumentationUnit.isEmpty()) {
      return Optional.empty();
    }

    DocumentationUnit documentationUnit = optionalDocumentationUnit.get();
    String xml = objectToLdmlConverterService.convertToLdml(
      documentationUnitContent,
      documentationUnit.xml()
    );

    return switch (documentationUnitContent) {
      case SliDocumentationUnitContent sli -> {
        String json = convertToJson(sli);
        DocumentationUnit publishedDocumentationUnit = documentationUnitPersistenceService.publish(
          documentNumber,
          json,
          xml
        );
        publishToPortal(documentNumber, xml, DocumentCategory.LITERATUR_SELBSTAENDIG);
        yield Optional.of(publishedDocumentationUnit);
      }
      case UliDocumentationUnitContent _ -> {
        publishToPortal(documentNumber, xml, DocumentCategory.LITERATUR_UNSELBSTAENDIG);
        // TODO: Return converted doc like for adm NOSONAR
        yield optionalDocumentationUnit;
      }
      case AdmDocumentationUnitContent adm -> {
        String json = convertToJson(adm);
        DocumentationUnit publishedDocumentationUnit = documentationUnitPersistenceService.publish(
          documentNumber,
          json,
          xml
        );
        publishToPortal(documentNumber, xml, DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
        yield convertLdml(publishedDocumentationUnit);
      }
      default -> throw new IllegalStateException(
        "Unsupported document category: " + documentationUnitContent.getClass().getSimpleName()
      );
    };
  }

  private void publishToPortal(
    @NotNull String documentNumber,
    String xml,
    DocumentCategory documentCategory
  ) {
    var publishOptions = new Publisher.PublicationDetails(documentNumber, xml, documentCategory);
    publisher.publish(publishOptions);
  }

  /**
   * Retrieves a paginated list of AdmDocumentationUnitOverviewElements based on the specified query options.
   *
   * @param queryOptions The query parameters to filter and paginate the results. Must not be null.
   * @return A paginated {@link Page} containing the matching AdmDocumentationUnitOverviewElements.
   */
  public Page<AdmDocumentationUnitOverviewElement> findAdmDocumentationUnitOverviewElements(
    @Nonnull AdmDocumentationUnitQuery queryOptions
  ) {
    return documentationUnitPersistenceService.findAdmDocumentationUnitOverviewElements(
      queryOptions
    );
  }

  /**
   * Retrieves a paginated list of LiteratureDocumentationUnitOverviewElements based on the specified query options.
   *
   * @param queryOptions The query parameters to filter and paginate the results. Must not be null.
   * @return A paginated {@link Page} containing the matching LiteratureDocumentationUnitOverviewElements.
   */
  public Page<
    LiteratureDocumentationUnitOverviewElement
  > findLiteratureDocumentationUnitOverviewElements(
    @Nonnull LiteratureDocumentationUnitQuery queryOptions
  ) {
    return documentationUnitPersistenceService.findLiteratureDocumentationUnitOverviewElements(
      queryOptions
    );
  }

  public Page<AdmAktivzitierungOverviewElement> findAktivzitierungen(AktivzitierungQuery query) {
    return schemaExecutor.executeInSchema(SchemaType.ADM, () ->
      documentationUnitPersistenceService.findAktivzitierungen(query)
    );
  }
}
