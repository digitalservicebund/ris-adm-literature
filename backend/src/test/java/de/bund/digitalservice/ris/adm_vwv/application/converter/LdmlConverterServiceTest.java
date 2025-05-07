package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Reference;
import de.bund.digitalservice.ris.adm_vwv.test.TestFile;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class LdmlConverterServiceTest {

  private final LdmlConverterService ldmlConverterService = new LdmlConverterService();

  @Test
  void convertToBusinessModel() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
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
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
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
      .containsOnly(tuple("Das Periodikum 2021, Seite 15", "Das Periodikum"));
  }

  @Test
  void convertToBusinessModel_longTitle() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
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
      .extracting(DocumentationUnitContent::langueberschrift)
      .isEqualTo("1. Bekanntmachung zum XML-Testen in NeuRIS VwV");
  }

  @Test
  void convertToBusinessModel_kurzreferat() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
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
    assertThat(documentationUnitContent.kurzreferat())
      .isNotNull()
      .containsSubsequence("<p>Kurzreferat Zeile 1</p>", "<p>Kurzreferat Zeile 2</p>");
  }

  @Test
  void convertToBusinessModel_entryIntoEffectDate() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
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
      .extracting(DocumentationUnitContent::inkrafttretedatum)
      .isEqualTo("2025-01-01");
  }

  @Test
  void convertToBusinessModel_expiryDate() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
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
      .extracting(DocumentationUnitContent::ausserkrafttretedatum)
      .isEqualTo("2025-02-02");
  }

  @Test
  void convertToBusinessModel_dateToQuote() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
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
      .extracting(DocumentationUnitContent::zitierdatum)
      .isEqualTo("2025-05-05");
  }
}
