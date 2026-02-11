package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaExecutor;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.TestAdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.LdmlToObjectConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ObjectToLdmlConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.TestLiteratureUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.Publisher;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.PublishingFailedException;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.ActiveReferenceService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.PassiveReferenceService;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import de.bund.digitalservice.ris.adm_literature.test.WithMockAdmUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WithMockAdmUser
class DocumentationUnitServiceTest {

  @InjectMocks
  private DocumentationUnitService documentationUnitService;

  @Mock
  private Publisher publisher;

  @Mock
  private DocumentationUnitPersistenceService documentationUnitPersistenceService;

  @Mock
  private LdmlToObjectConverterService ldmlToObjectConverterService;

  @Mock
  private ObjectToLdmlConverterService objectToLdmlConverterService;

  @Spy
  private SchemaExecutor schemaExecutor;

  @Mock
  private ActiveReferenceService activeReferenceService;

  @Mock
  private PassiveReferenceService passiveReferenceService;

  @Spy
  private ObjectMapper objectMapper;

  @Captor
  private ArgumentCaptor<Publisher.PublicationDetails> publicationDetailsCaptor;

  private static final String TEST_OLD_XML = "<xml>old content</xml>";
  private static final String TEST_NEW_XML = "<xml>new content</xml>";
  private static final String TEST_JSON = "{\"key\":\"value\"}";

  @Test
  void findByDocumentNumber() {
    // given
    String xml =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <akn:akomaNtoso
        xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
        xmlns:ris="http://ldml.neuris.de/meta/">
        <akn:doc name="offene-struktur">
          <akn:meta>
            <akn:proprietary>
              <ris:meta>
                <ris:documentType category="VV" longTitle="Verwaltungsvorschrift">VV Verwaltungsvorschrift</ris:documentType>
              </ris:meta>
            </akn:proprietary>
          </akn:meta>
        </akn:doc>
      </akn:akomaNtoso>""";
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR2025000001",
      UUID.randomUUID(),
      null,
      xml,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KSNR2025000001")).willReturn(
      Optional.of(documentationUnit)
    );
    given(
      ldmlToObjectConverterService.convertToBusinessModel(
        documentationUnit,
        AdmDocumentationUnitContent.class
      )
    ).willReturn(TestAdmDocumentationUnitContent.create("KSNR2025000001", null));

    // when
    Optional<DocumentationUnit> actual = documentationUnitService.findByDocumentNumber(
      "KSNR2025000001"
    );

    // then
    assertThat(actual)
      .isPresent()
      .get()
      .extracting(DocumentationUnit::json, InstanceOfAssertFactories.STRING)
      .contains(
        """
        dokumenttyp":{"abbreviation":"VR","name":"Verwaltungsregelung"}"""
      );
  }

  @Test
  void findByDocumentNumber_notValidXml() throws JacksonException {
    // given
    String xml =
      """
      <?xml version="1.0" encoding="UTF-8"?>""";
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR2025000001",
      UUID.randomUUID(),
      null,
      xml,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KSNR2025000001")).willReturn(
      Optional.of(documentationUnit)
    );
    given(
      ldmlToObjectConverterService.convertToBusinessModel(
        documentationUnit,
        AdmDocumentationUnitContent.class
      )
    ).willReturn(null);
    given(objectMapper.writeValueAsString(null)).willThrow(JacksonException.class);

    // when
    Exception exception = catchException(() ->
      documentationUnitService.findByDocumentNumber("KSNR2025000001")
    );

    // then
    assertThat(exception).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void findByDocumentNumber_doesNotExist() {
    // given
    given(documentationUnitPersistenceService.findByDocumentNumber("KSNR112233445566")).willReturn(
      Optional.empty()
    );

    // when
    Optional<DocumentationUnit> actual = documentationUnitService.findByDocumentNumber(
      "KSNR112233445566"
    );

    // then
    assertThat(actual).isEmpty();
  }

  @Test
  void publish_shouldCallDatabaseAndS3Port_onHappyPath() {
    String docNumber = "doc123";
    String fakeXml = "<test>xml</test>";
    String fakeJson = "{\"test\":\"json\"}";

    var doc = new DocumentationUnit(
      docNumber,
      UUID.randomUUID(),
      null,
      fakeXml,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );
    var content = TestAdmDocumentationUnitContent.create(docNumber, "Lange Überschrift");
    var publishedDoc = new DocumentationUnit(
      docNumber,
      UUID.randomUUID(),
      fakeJson,
      fakeXml,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );

    when(documentationUnitPersistenceService.findByDocumentNumber(docNumber)).thenReturn(
      Optional.of(doc)
    );
    when(objectToLdmlConverterService.convertToLdml(any(), any(), any())).thenReturn(fakeXml);
    when(documentationUnitPersistenceService.publish(any(), any(), any())).thenReturn(publishedDoc);

    documentationUnitService.publish(docNumber, content);

    verify(documentationUnitPersistenceService).publish(eq(docNumber), anyString(), eq(fakeXml));
    verify(publisher).publish(any(Publisher.PublicationDetails.class));
  }

  @Test
  void publish_shouldThrowExceptionAndRollback_whenExternalPublishingFails() {
    // given
    DocumentationUnit sampleDocUnit = new DocumentationUnit(
      "KSNR123456789",
      UUID.randomUUID(),
      null,
      null,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );

    // when
    when(
      documentationUnitPersistenceService.create(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
    ).thenReturn(sampleDocUnit);
    when(documentationUnitPersistenceService.findByDocumentNumber(anyString())).thenReturn(
      Optional.of(sampleDocUnit)
    );

    DocumentationUnit documentationUnit = documentationUnitService.create(
      DocumentCategory.VERWALTUNGSVORSCHRIFTEN
    );
    String documentNumber = documentationUnit.documentNumber();
    assertThat(documentationUnit.json()).isNull();

    doThrow(new PublishingFailedException("External system is down", null))
      .when(publisher)
      .publish(any(Publisher.PublicationDetails.class));

    // then
    assertThatThrownBy(() ->
      documentationUnitService.publish(
        documentNumber,
        TestAdmDocumentationUnitContent.create(documentNumber, "Some Content")
      )
    ).isInstanceOf(PublishingFailedException.class);

    Optional<DocumentationUnit> actual = documentationUnitService.findByDocumentNumber(
      documentNumber
    );
    assertThat(actual).isPresent().hasValueSatisfying(dun -> assertThat(dun.json()).isNull());
  }

  @Test
  void publish_shouldUseBsgPublisher_whenCategoryIsVerwaltungsvorschriften() {
    // given
    given(
      passiveReferenceService.findByDocumentNumber(
        "KSNR1234567890",
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN
      )
    ).willReturn(List.of());
    DocumentationUnit existingUnit = new DocumentationUnit(
      "KSNR1234567890",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_OLD_XML,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );
    DocumentationUnit publishedUnit = new DocumentationUnit(
      "KSNR1234567890",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_NEW_XML,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );
    AdmDocumentationUnitContent contentToPublish = TestAdmDocumentationUnitContent.create(
      "KSNR1234567890",
      null
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KSNR1234567890")).willReturn(
      Optional.of(existingUnit)
    );
    given(
      objectToLdmlConverterService.convertToLdml(contentToPublish, TEST_OLD_XML, List.of())
    ).willReturn(TEST_NEW_XML);
    given(objectMapper.writeValueAsString(contentToPublish)).willReturn(TEST_JSON);
    given(
      documentationUnitPersistenceService.publish("KSNR1234567890", TEST_JSON, TEST_NEW_XML)
    ).willReturn(publishedUnit);
    given(
      ldmlToObjectConverterService.convertToBusinessModel(
        publishedUnit,
        AdmDocumentationUnitContent.class
      )
    ).willReturn(contentToPublish);

    // when
    Optional<DocumentationUnit> result = documentationUnitService.publish(
      "KSNR1234567890",
      contentToPublish
    );

    // then
    assertThat(result).isPresent();
    verify(publisher).publish(publicationDetailsCaptor.capture());
    assertThat(publicationDetailsCaptor.getValue().category().getPublisherName()).isEqualTo(
      "publicBsgPublisher"
    );
  }

  @Test
  void publish_shouldUseLiteraturePublisher_whenCategoryIsUli() {
    // given
    given(
      passiveReferenceService.findByDocumentNumber(
        "KALU123456789",
        DocumentCategory.LITERATUR_UNSELBSTAENDIG
      )
    ).willReturn(List.of());
    DocumentationUnit existingUnit = new DocumentationUnit(
      "KALU123456789",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_OLD_XML,
      new AdministrativeData(DocumentCategory.LITERATUR_UNSELBSTAENDIG, null)
    );
    DocumentationUnit publishedUnit = new DocumentationUnit(
      "KALU123456789",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_NEW_XML,
      new AdministrativeData(DocumentCategory.LITERATUR_UNSELBSTAENDIG, null)
    );
    UliDocumentationUnitContent contentToPublish = TestLiteratureUnitContent.createUli(
      "KALU123456789",
      "2025"
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KALU123456789")).willReturn(
      Optional.of(existingUnit)
    );
    given(objectMapper.writeValueAsString(contentToPublish)).willReturn(TEST_JSON);
    given(
      documentationUnitPersistenceService.publish("KALU123456789", TEST_JSON, TEST_NEW_XML)
    ).willReturn(publishedUnit);
    given(
      objectToLdmlConverterService.convertToLdml(contentToPublish, TEST_OLD_XML, List.of())
    ).willReturn(TEST_NEW_XML);
    given(
      ldmlToObjectConverterService.convertToBusinessModel(
        publishedUnit,
        UliDocumentationUnitContent.class
      )
    ).willReturn(contentToPublish);

    // when
    Optional<DocumentationUnit> result = documentationUnitService.publish(
      "KALU123456789",
      contentToPublish
    );

    // then
    assertThat(result).isPresent();
    verify(publisher).publish(publicationDetailsCaptor.capture());
    assertThat(publicationDetailsCaptor.getValue().category().getPublisherName()).isEqualTo(
      "publicLiteraturePublisher"
    );
  }

  @Test
  void publish_shouldUseLiteraturePublisher_whenCategoryIsSli() {
    // given
    given(
      passiveReferenceService.findByDocumentNumber(
        "KVLS123456789",
        DocumentCategory.LITERATUR_SELBSTAENDIG
      )
    ).willReturn(List.of());
    DocumentationUnit existingUnit = new DocumentationUnit(
      "KVLS123456789",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_OLD_XML,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );
    DocumentationUnit publishedUnit = new DocumentationUnit(
      "KVLS123456789",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_NEW_XML,
      new AdministrativeData(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, null)
    );
    SliDocumentationUnitContent contentToPublish = TestLiteratureUnitContent.createSli(
      "KVLS123456789",
      "2025"
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KVLS123456789")).willReturn(
      Optional.of(existingUnit)
    );
    given(
      objectToLdmlConverterService.convertToLdml(contentToPublish, TEST_OLD_XML, List.of())
    ).willReturn(TEST_NEW_XML);

    given(objectMapper.writeValueAsString(contentToPublish)).willReturn(TEST_JSON);
    given(
      documentationUnitPersistenceService.publish("KVLS123456789", TEST_JSON, TEST_NEW_XML)
    ).willReturn(publishedUnit);

    // when
    Optional<DocumentationUnit> result = documentationUnitService.publish(
      "KVLS123456789",
      contentToPublish
    );

    // then
    assertThat(result).isPresent();
    verify(publisher).publish(publicationDetailsCaptor.capture());
    assertThat(publicationDetailsCaptor.getValue().category().getPublisherName()).isEqualTo(
      "publicLiteraturePublisher"
    );
  }

  @Test
  @DisplayName("findSliDocumentationUnitOverviewElements should delegate to persistence service")
  void findSliDocumentationUnitOverviewElements_shouldDelegateToPersistence() {
    // given
    SliDocumentationUnitQuery query = new SliDocumentationUnitQuery(
      "docNum",
      "2024",
      List.of("Bib"),
      "Titel",
      List.of("Schmidt"),
      new QueryOptions(0, 10, "test", null, false)
    );

    Page<SliDocumentationUnitOverviewElement> expectedPage = mock(Page.class);

    given(
      documentationUnitPersistenceService.findSliDocumentationUnitOverviewElements(query)
    ).willReturn(expectedPage);

    // when
    Page<SliDocumentationUnitOverviewElement> result =
      documentationUnitService.findSliDocumentationUnitOverviewElements(query);

    // then
    assertThat(result).isSameAs(expectedPage);
    verify(documentationUnitPersistenceService).findSliDocumentationUnitOverviewElements(query);
  }

  @Test
  @DisplayName("findUliDocumentationUnitOverviewElements should delegate to persistence service")
  void findUliDocumentationUnitOverviewElements_shouldDelegateToPersistence() {
    // given
    UliDocumentationUnitQuery query = new UliDocumentationUnitQuery(
      "docNum",
      "NJW",
      "S. 123",
      List.of("Aufsatz"),
      List.of("Author"),
      new QueryOptions(0, 10, "test", null, false)
    );

    Page<UliDocumentationUnitOverviewElement> expectedPage = mock(Page.class);

    given(
      documentationUnitPersistenceService.findUliDocumentationUnitOverviewElements(query)
    ).willReturn(expectedPage);

    // when
    Page<UliDocumentationUnitOverviewElement> result =
      documentationUnitService.findUliDocumentationUnitOverviewElements(query);

    // then
    assertThat(result).isSameAs(expectedPage);
    verify(documentationUnitPersistenceService).findUliDocumentationUnitOverviewElements(query);
  }
}
