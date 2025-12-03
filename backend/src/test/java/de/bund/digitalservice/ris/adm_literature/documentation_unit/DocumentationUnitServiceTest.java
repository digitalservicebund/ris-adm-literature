package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.LdmlConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.LdmlPublishConverterService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.Publisher;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.PublishingFailedException;
import de.bund.digitalservice.ris.adm_literature.test.WithMockAdmUser;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
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
  private LdmlConverterService ldmlConverterService;

  @Mock
  private LdmlPublishConverterService ldmlPublishConverterService;

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
      xml
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KSNR2025000001")).willReturn(
      Optional.of(documentationUnit)
    );
    given(ldmlConverterService.convertToBusinessModel(documentationUnit)).willReturn(
      TestAdmDocumentationUnitContent.create("KSNR2025000001", null)
    );

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
      xml
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KSNR2025000001")).willReturn(
      Optional.of(documentationUnit)
    );
    given(ldmlConverterService.convertToBusinessModel(documentationUnit)).willReturn(null);
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

    var doc = new DocumentationUnit(docNumber, UUID.randomUUID(), null, fakeXml);
    var content = TestAdmDocumentationUnitContent.create(docNumber, "Lange Überschrift");
    var publishedDoc = new DocumentationUnit(docNumber, UUID.randomUUID(), fakeJson, fakeXml);

    when(documentationUnitPersistenceService.findByDocumentNumber(docNumber)).thenReturn(
      Optional.of(doc)
    );
    when(ldmlPublishConverterService.convertToLdml(any(), any())).thenReturn(fakeXml);
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
      null
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
    DocumentationUnit existingUnit = new DocumentationUnit(
      "KSNR1234567890",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_OLD_XML
    );
    DocumentationUnit publishedUnit = new DocumentationUnit(
      "KSNR1234567890",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_NEW_XML
    );
    AdmDocumentationUnitContent contentToPublish = TestAdmDocumentationUnitContent.create(
      "KSNR1234567890",
      null
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KSNR1234567890")).willReturn(
      Optional.of(existingUnit)
    );
    given(ldmlPublishConverterService.convertToLdml(contentToPublish, TEST_OLD_XML)).willReturn(
      TEST_NEW_XML
    );
    given(objectMapper.writeValueAsString(contentToPublish)).willReturn(TEST_JSON);
    given(
      documentationUnitPersistenceService.publish("KSNR1234567890", TEST_JSON, TEST_NEW_XML)
    ).willReturn(publishedUnit);
    given(ldmlConverterService.convertToBusinessModel(publishedUnit)).willReturn(contentToPublish);

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
    DocumentationUnit existingUnit = new DocumentationUnit(
      "KALU123456789",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_OLD_XML
    );
    UliDocumentationUnitContent contentToPublish = TestDocumentationUnitContent.createUli(
      "KALU123456789",
      "2025"
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KALU123456789")).willReturn(
      Optional.of(existingUnit)
    );
    given(ldmlPublishConverterService.convertToLdml(contentToPublish, TEST_OLD_XML)).willReturn(
      TEST_NEW_XML
    );

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
    DocumentationUnit existingUnit = new DocumentationUnit(
      "KVLS123456789",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_OLD_XML
    );
    DocumentationUnit publishedUnit = new DocumentationUnit(
      "KSNR1234567890",
      UUID.randomUUID(),
      TEST_JSON,
      TEST_NEW_XML
    );
    SliDocumentationUnitContent contentToPublish = TestDocumentationUnitContent.createSli(
      "KVLS123456789",
      "2025"
    );
    given(documentationUnitPersistenceService.findByDocumentNumber("KVLS123456789")).willReturn(
      Optional.of(existingUnit)
    );
    given(ldmlPublishConverterService.convertToLdml(contentToPublish, TEST_OLD_XML)).willReturn(
      TEST_NEW_XML
    );

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
}
