package de.bund.digitalservice.ris.adm_vwv.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.adm_vwv.adapter.publishing.PublishPort;
import de.bund.digitalservice.ris.adm_vwv.application.converter.LdmlConverterService;
import de.bund.digitalservice.ris.adm_vwv.application.converter.LdmlPublishConverterService;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.TestDocumentationUnitContent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class DocumentationUnitServiceTest {

  @InjectMocks
  private DocumentationUnitService documentationUnitService;

  @Mock
  private PublishPort publishPort;

  @Mock
  private Map<String, PublishPort> publishers;

  @Mock
  private DocumentationUnitPersistencePort documentationUnitPersistencePort;

  @Mock
  private LdmlConverterService ldmlConverterService;

  @Mock
  private LdmlPublishConverterService ldmlPublishConverterService;

  @Spy
  private ObjectMapper objectMapper;

  @Test
  void findByDocumentNumber() {
    // given
    String xml =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <akn:akomaNtoso
        xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
        xmlns:ris="http://ldml.neuris.de/metadata/">
        <akn:doc name="offene-struktur">
          <akn:meta>
            <akn:proprietary>
              <ris:metadata>
                <ris:documentType category="VV" longTitle="Verwaltungsvorschrift">VV Verwaltungsvorschrift</ris:documentType>
              </ris:metadata>
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
    given(documentationUnitPersistencePort.findByDocumentNumber("KSNR2025000001")).willReturn(
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
    given(documentationUnitPersistencePort.findByDocumentNumber("KSNR2025000001")).willReturn(
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
    given(documentationUnitPersistencePort.findByDocumentNumber("KSNR112233445566")).willReturn(
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
      List.of()
    );
  }

  @Test
  void publish_shouldCallDatabaseAndS3Port_onHappyPath() {
    String DOC_NUMBER = "doc123";
    String FAKE_XML = "<test>xml</test>";
    String BSG_PUBLISHER_NAME = "privateBsgPublisher";
    String FAKE_JSON = "{\"test\":\"json\"}";

    var doc = new DocumentationUnit(DOC_NUMBER, UUID.randomUUID(), null, FAKE_XML);
    var content = TestDocumentationUnitContent.create(DOC_NUMBER, "Lange Überschrift");
    var publishedDoc = new DocumentationUnit(DOC_NUMBER, UUID.randomUUID(), FAKE_JSON, FAKE_XML);

    when(documentationUnitPersistencePort.findByDocumentNumber(DOC_NUMBER)).thenReturn(
      Optional.of(doc)
    );
    when(ldmlPublishConverterService.convertToLdml(any(), any())).thenReturn(FAKE_XML);
    when(documentationUnitPersistencePort.publish(any(), any(), any())).thenReturn(publishedDoc);
    when(publishers.get(BSG_PUBLISHER_NAME)).thenReturn(publishPort);

    documentationUnitService.publish(DOC_NUMBER, content);

    verify(documentationUnitPersistencePort).publish(eq(DOC_NUMBER), anyString(), eq(FAKE_XML));
    verify(publishPort).publish(any(PublishPort.Options.class));
  }
}
