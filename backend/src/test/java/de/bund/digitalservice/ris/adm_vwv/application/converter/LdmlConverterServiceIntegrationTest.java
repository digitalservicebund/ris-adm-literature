package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.*;
import de.bund.digitalservice.ris.adm_vwv.test.TestFile;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LdmlConverterServiceIntegrationTest {

  @Autowired
  private LdmlConverterService ldmlConverterService;

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
      .containsOnly(tuple("2021, Seite 15", "Das Periodikum"));
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
      .startsWith("<p>Kurzreferat Zeile 1</p>")
      .endsWith("<p>Kurzreferat Zeile 2</p>");
  }

  @Test
  @Tag("RISDEV-7821")
  void convertToBusinessModel_noKurzreferat() {
    // given
    String xml = TestFile.readFileToString("ldml-no-kurzreferat.akn.xml");
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
    assertThat(documentationUnitContent.kurzreferat()).isNull();
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
  void convertToBusinessModel_gliederung() {
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
      .extracting(DocumentationUnitContent::gliederung)
      .isEqualTo(
        """
        <p>TOC entry 1</p>
        <p>TOC entry 2</p>"""
      );
  }

  @Test
  void convertToBusinessModel_gliederung_nonexistent() {
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
      .extracting(DocumentationUnitContent::gliederung)
      .isNull();
  }

  @Test
  void convertToBusinessModel_keywords() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    List<String> expectedKeywords = Stream.of(
      "Schlag",
      "Wort",
      "Mehrere Wörter in einem Schlagwort"
    ).toList();

    // when
    DocumentationUnitContent documentationUnitContent = ldmlConverterService.convertToBusinessModel(
      documentationUnit
    );

    // then
    assertThat(documentationUnitContent)
      .isNotNull()
      .extracting(DocumentationUnitContent::keywords)
      .isEqualTo(expectedKeywords);
  }

  @Test
  void convertToBusinessModel_keywords_empty() {
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
      .extracting(DocumentationUnitContent::keywords)
      .isEqualTo(List.of());
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

  @Test
  void convertToBusinessModel_referenceNumbers() {
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
      .extracting(DocumentationUnitContent::aktenzeichen, DocumentationUnitContent::noAktenzeichen)
      .containsExactly(List.of("AX-Y12345", "XX"), false);
  }

  @Test
  void convertToBusinessModel_referenceNumbers_empty() {
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
              </ris:metadata>
            </akn:proprietary>
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
      .extracting(DocumentationUnitContent::aktenzeichen, DocumentationUnitContent::noAktenzeichen)
      .containsExactly(List.of(), true);
  }

  @Test
  void convertToBusinessModel_fieldsOfLaw() {
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
    assertThat(
      documentationUnitContent.fieldsOfLaw().stream().map(FieldOfLaw::text).toList()
    ).isEqualTo(List.of("PR-05-01", "XX-04-02"));
  }

  @Test
  void convertToBusinessModel_documentType() {
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
      .extracting(DocumentationUnitContent::dokumenttyp)
      .extracting(DocumentType::abbreviation, DocumentType::name)
      .containsExactly("VR", "Verwaltungsregelung");
  }

  @Test
  void convertToBusinessModel_documentTypeZusatz() {
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
      .extracting(DocumentationUnitContent::dokumenttypZusatz)
      .isEqualTo("Bekanntmachung");
  }

  @Test
  void convertToBusinessModel_normReference() {
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
    assertThat(documentationUnitContent.normReferences())
      .hasSize(2)
      .first()
      .extracting(
        nr -> nr.normAbbreviation().abbreviation(),
        nr -> nr.normAbbreviation().officialLongTitle(),
        nr -> nr.singleNorms().getFirst().singleNorm(),
        nr -> nr.singleNorms().getFirst().dateOfVersion(),
        nr -> nr.singleNorms().getFirst().dateOfRelevance()
      )
      .containsExactly("PhanGB", "PhanGB § 1a Abs 1", "§ 1a Abs 1", "2022-02-02", "2011");
  }

  @Test
  void convertToBusinessModel_activeCitations() {
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
      .extracting(
        DocumentationUnitContent::activeCitations,
        InstanceOfAssertFactories.list(ActiveCitation.class)
      )
      .hasSize(1)
      .first()
      .extracting(
        ac -> ac.citationType().jurisShortcut(),
        ac -> ac.court().label(),
        ActiveCitation::decisionDate,
        ActiveCitation::fileNumber,
        ActiveCitation::documentNumber
      )
      .containsExactly("Vgl", "PhanGH", "2021-10-20", "C-01/02", "WBRE000001234");
  }

  @Test
  void convertToBusinessModel_activeReferences() {
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
      .extracting(
        DocumentationUnitContent::activeReferences,
        InstanceOfAssertFactories.list(ActiveReference.class)
      )
      .hasSize(2)
      .extracting(
        ActiveReference::referenceDocumentType,
        ActiveReference::normAbbreviationRawValue,
        ActiveReference::referenceType,
        activeReference ->
          activeReference.singleNorms().stream().map(SingleNorm::singleNorm).toList()
      )
      .containsExactly(
        tuple("administrative_regulation", "PhanGB", "rechtsgrundlage", List.of("§ 1a Abs 1")),
        tuple("administrative_regulation", "PhanGB", "rechtsgrundlage", List.of("§ 2 Abs 6"))
      );
  }
}
