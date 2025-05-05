package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LdmlConverterServiceTest {

  private final LdmlConverterService ldmlConverterService = new LdmlConverterService();

  @Test
  void convertToBusinessModel() {
    // given
    String xml =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <akn:akomaNtoso
        xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
        xmlns:ris="http://ldml.neuris.de/metadata/">
        <akn:doc name="offene-struktur">
        </akn:doc>
      </akn:akomaNtoso>
      """;
    UUID uuid = UUID.randomUUID();
    DocumentationUnit documentationUnit = new DocumentationUnit("KSNR20250000001", uuid, null, xml);

    // when
    DocumentationUnitContent documentationUnitContent = ldmlConverterService.convertToBusinessModel(
      documentationUnit
    );

    // then
    assertThat(documentationUnitContent)
      .isNotNull()
      .extracting(DocumentationUnitContent::id, DocumentationUnitContent::documentNumber)
      .containsExactly(uuid, "KSNR20250000001");
  }
}
