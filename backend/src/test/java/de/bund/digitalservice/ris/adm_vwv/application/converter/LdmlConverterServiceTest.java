package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Reference;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
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
          <akn:meta>
          </akn:meta>
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

  @Test
  void convertToBusinessModel_fundstellen() {
    // given
    String xml =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <akn:akomaNtoso
        xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
        xmlns:ris="http://ldml.neuris.de/metadata/">
        <akn:doc name="offene-struktur">
          <akn:meta>
            <akn:analysis>
              <akn:otherReferences>
                  <akn:implicitReference showAs="2020, Seite 5" shortForm="BAnz" />
              </akn:otherReferences>
            </akn:analysis>
          </akn:meta>
        </akn:doc>
      </akn:akomaNtoso>
      """;
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    DocumentationUnitContent documentationUnitContent = ldmlConverterService.convertToBusinessModel(
      documentationUnit
    );

    // then
    assertThat(documentationUnitContent)
      .isNotNull()
      .extracting(DocumentationUnitContent::references)
      .asInstanceOf(InstanceOfAssertFactories.list(Reference.class))
      .extracting(Reference::citation, Reference::legalPeriodicalRawValue)
      .containsOnly(Assertions.tuple("2020, Seite 5", "BAnz"));
  }
}
