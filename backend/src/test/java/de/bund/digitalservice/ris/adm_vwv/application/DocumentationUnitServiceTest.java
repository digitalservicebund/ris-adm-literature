package de.bund.digitalservice.ris.adm_vwv.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentationUnitPersistenceService;
import de.bund.digitalservice.ris.adm_vwv.adapter.publishing.Publisher;
import de.bund.digitalservice.ris.adm_vwv.application.converter.LdmlConverterService;
import de.bund.digitalservice.ris.adm_vwv.application.converter.LdmlPublishConverterService;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.TestDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.test.WithMockAdmUser;
import de.bund.digitalservice.ris.adm_vwv.test.WithMockLitUser;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@WithMockAdmUser
class DocumentationUnitServiceTest {

  @InjectMocks
  private DocumentationUnitService documentationUnitService;

  @Mock
  private Publisher publisher;

  @Mock
  private Map<String, Publisher> publishers;

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

  private static final String TEST_DOC_NUMBER = "KALU123456789";
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
      createDocumentationUnitContent()
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
        dokumenttyp":{"abbreviation":"VV","name":"Verwaltungsvorschrift"}"""
      );
  }

  @Test
  void findByDocumentNumber_notValidXml()
    throws com.fasterxml.jackson.core.JsonProcessingException {
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
    given(objectMapper.writeValueAsString(null)).willThrow(JsonProcessingException.class);

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

  private static DocumentationUnitContent createDocumentationUnitContent() {
    return new DocumentationUnitContent(
      UUID.randomUUID(),
      "KSNR2025000001",
      List.of(),
      List.of(),
      null,
      List.of(),
      List.of(),
      null,
      null,
      null,
      null,
      List.of(),
      false,
      new DocumentType("VV", "Verwaltungsvorschrift"),
      "Verwaltungsvorschrift",
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(),
      List.of(),
      List.of(),
      List.of()
    );
  }

  @Test
  void publish_shouldCallDatabaseAndS3Port_onHappyPath() {
    String docNumber = "doc123";
    String fakeXml = "<test>xml</test>";
    String bsgPublisherName = "publicBsgPublisher";
    String fakeJson = "{\"test\":\"json\"}";

    var doc = new DocumentationUnit(docNumber, UUID.randomUUID(), null, fakeXml);
    var content = TestDocumentationUnitContent.create(docNumber, "Lange Überschrift");
    var publishedDoc = new DocumentationUnit(docNumber, UUID.randomUUID(), fakeJson, fakeXml);

    when(documentationUnitPersistenceService.findByDocumentNumber(docNumber)).thenReturn(
      Optional.of(doc)
    );
    when(ldmlPublishConverterService.convertToLdml(any(), any())).thenReturn(fakeXml);
    when(documentationUnitPersistenceService.publish(any(), any(), any())).thenReturn(publishedDoc);
    when(publishers.get(bsgPublisherName)).thenReturn(publisher);

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
    when(documentationUnitPersistenceService.create()).thenReturn(sampleDocUnit);
    when(documentationUnitPersistenceService.findByDocumentNumber(anyString())).thenReturn(
      Optional.of(sampleDocUnit)
    );

    DocumentationUnit documentationUnit = documentationUnitService.create();
    String documentNumber = documentationUnit.documentNumber();
    assertThat(documentationUnit.json()).isNull();

    doThrow(new PublishingFailedException("External system is down", null))
      .when(publisher)
      .publish(any(Publisher.PublicationDetails.class));

    // then
    assertThatThrownBy(() ->
      documentationUnitService.publish(
        documentNumber,
        TestDocumentationUnitContent.create(documentNumber, "Some Content")
      )
    ).isInstanceOf(PublishingFailedException.class);

    Optional<DocumentationUnit> actual = documentationUnitService.findByDocumentNumber(
      documentNumber
    );
    assertThat(actual).isPresent().hasValueSatisfying(dun -> assertThat(dun.json()).isNull());
  }

  @Test
  @WithMockAdmUser
  void publish_shouldUseBsgPublisher_whenCategoryIsVerwaltungsvorschriften() throws Exception {
    DocumentationUnit existingUnit = new DocumentationUnit(
      TEST_DOC_NUMBER,
      UUID.randomUUID(),
      TEST_JSON,
      TEST_OLD_XML
    );
    DocumentationUnit publishedUnit = new DocumentationUnit(
      TEST_DOC_NUMBER,
      UUID.randomUUID(),
      TEST_JSON,
      TEST_NEW_XML
    );
    DocumentationUnitContent contentToPublish = createDocumentationUnitContent();

    when(documentationUnitPersistenceService.findByDocumentNumber(TEST_DOC_NUMBER)).thenReturn(
      Optional.of(existingUnit)
    );
    when(ldmlPublishConverterService.convertToLdml(contentToPublish, TEST_OLD_XML)).thenReturn(
      TEST_NEW_XML
    );
    when(objectMapper.writeValueAsString(contentToPublish)).thenReturn(TEST_JSON);
    when(
      documentationUnitPersistenceService.publish(TEST_DOC_NUMBER, TEST_JSON, TEST_NEW_XML)
    ).thenReturn(publishedUnit);
    when(ldmlConverterService.convertToBusinessModel(publishedUnit)).thenReturn(contentToPublish);

    Optional<DocumentationUnit> result = documentationUnitService.publish(
      TEST_DOC_NUMBER,
      contentToPublish
    );

    verify(publisher).publish(publicationDetailsCaptor.capture());
    Publisher.PublicationDetails details = publicationDetailsCaptor.getValue();

    assertThat(details.targetPublisher()).isEqualTo("publicBsgPublisher");
    assertThat(result).isPresent();
  }

  @Test
  @WithMockLitUser
  void publish_shouldUseLiteraturePublisher_whenCategoryIsLiteratur() throws Exception {
    DocumentationUnit existingUnit = new DocumentationUnit(
      TEST_DOC_NUMBER,
      UUID.randomUUID(),
      TEST_JSON,
      TEST_OLD_XML
    );
    DocumentationUnit publishedUnit = new DocumentationUnit(
      TEST_DOC_NUMBER,
      UUID.randomUUID(),
      TEST_JSON,
      TEST_NEW_XML
    );
    DocumentationUnitContent contentToPublish = createDocumentationUnitContent();

    when(documentationUnitPersistenceService.findByDocumentNumber(TEST_DOC_NUMBER)).thenReturn(
      Optional.of(existingUnit)
    );
    when(ldmlPublishConverterService.convertToLdml(contentToPublish, TEST_OLD_XML)).thenReturn(
      TEST_NEW_XML
    );
    when(objectMapper.writeValueAsString(contentToPublish)).thenReturn(TEST_JSON);
    when(
      documentationUnitPersistenceService.publish(TEST_DOC_NUMBER, TEST_JSON, TEST_NEW_XML)
    ).thenReturn(publishedUnit);
    when(ldmlConverterService.convertToBusinessModel(publishedUnit)).thenReturn(contentToPublish);

    // ACT
    Optional<DocumentationUnit> result = documentationUnitService.publish(
      TEST_DOC_NUMBER,
      contentToPublish
    );

    // ASSERT
    verify(publisher).publish(publicationDetailsCaptor.capture());
    Publisher.PublicationDetails details = publicationDetailsCaptor.getValue();

    // The logic correctly uses the literature client
    assertThat(details.targetPublisher()).isEqualTo("publicLiteraturePublisher");
    assertThat(result).isPresent();
  }
}
