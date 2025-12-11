package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.Fundstelle;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law.FieldOfLaw;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.InstitutionType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.Region;
import de.bund.digitalservice.ris.adm_literature.test.TestFile;
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
class LdmlToAdmConverterServiceIntegrationTest {

  @Autowired
  private LdmlToAdmConverterService ldmlToAdmConverterService;

  @Test
  void convertToBusinessModel() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    UUID uuid = UUID.randomUUID();
    DocumentationUnit documentationUnit = new DocumentationUnit("KSNR20250000001", uuid, null, xml);

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::id, AdmDocumentationUnitContent::documentNumber)
      .containsExactly(uuid, "KSNR20250000001");
  }

  @Test
  void convertToBusinessModel_fundstellen() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::fundstellen)
      .asInstanceOf(InstanceOfAssertFactories.list(Fundstelle.class))
      .extracting(Fundstelle::zitatstelle, Fundstelle::ambiguousPeriodikum)
      .containsOnly(tuple("2021, Seite 15", "Das Periodikum"));
  }

  @Test
  void convertToBusinessModel_longTitle() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::langueberschrift)
      .isEqualTo("1. Bekanntmachung zum XML-Testen in NeuRIS VwV");
  }

  @Test
  void convertToBusinessModel_kurzreferat() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent.kurzreferat())
      .isNotNull()
      .startsWith("<p>Kurzreferat Zeile 1</p>")
      .endsWith("<p>Kurzreferat Zeile 2</p>");
  }

  @Test
  @Tag("RISDEV-7821")
  void convertToBusinessModel_noKurzreferat() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-no-kurzreferat.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent.kurzreferat()).isNull();
  }

  @Test
  void convertToBusinessModel_entryIntoEffectDate() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::inkrafttretedatum)
      .isEqualTo("2025-01-01");
  }

  @Test
  void convertToBusinessModel_expiryDate() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::ausserkrafttretedatum)
      .isEqualTo("2025-02-02");
  }

  @Test
  void convertToBusinessModel_gliederung() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::gliederung)
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
        xmlns:ris="http://ldml.neuris.de/meta/">
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
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::gliederung)
      .isNull();
  }

  @Test
  void convertToBusinessModel_keywords() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
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
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::keywords)
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
        xmlns:ris="http://ldml.neuris.de/meta/">
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
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::keywords)
      .isEqualTo(List.of());
  }

  @Test
  void convertToBusinessModel_datesToQuote() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::zitierdaten)
      .asInstanceOf(InstanceOfAssertFactories.list(String.class))
      .containsExactly("2025-05-05", "2025-06-01");
  }

  @Test
  void convertToBusinessModel_referenceNumbers() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::aktenzeichen)
      .asInstanceOf(InstanceOfAssertFactories.list(String.class))
      .containsExactly("AX-Y12345", "XX");
  }

  @Test
  void convertToBusinessModel_referenceNumbers_empty() {
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
              </ris:meta>
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
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::aktenzeichen)
      .asInstanceOf(InstanceOfAssertFactories.list(String.class))
      .isEmpty();
  }

  @Test
  void convertToBusinessModel_fieldsOfLaw() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent.fieldsOfLaw())
      .hasSize(2)
      .extracting(FieldOfLaw::identifier, FieldOfLaw::text)
      .containsExactly(
        tuple("PR-05-01", "Phantasie besonderer Art, Ansprüche anderer Art"),
        tuple(
          "XX-04-02",
          """
          Versicherter Personenkreis: Versicherungspflicht und Beitragspflicht; Beginn, Ende, Fortbestand \
          der Mitgliedschaft siehe XX-05-07-04 bis -05"""
        )
      );
  }

  @Test
  void convertToBusinessModel_documentType() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::dokumenttyp)
      .extracting(DocumentType::abbreviation, DocumentType::name)
      .containsExactly("VR", "Verwaltungsregelung");
  }

  @Test
  void convertToBusinessModel_documentTypeZusatz() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::dokumenttypZusatz)
      .isEqualTo("Bekanntmachung");
  }

  @Test
  void convertToBusinessModel_normReference() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent.normReferences())
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
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(
        AdmDocumentationUnitContent::activeCitations,
        InstanceOfAssertFactories.list(ActiveCitation.class)
      )
      .hasSize(1)
      .first()
      .extracting(
        ac -> ac.zitierArt().abbreviation(),
        ac -> ac.zitierArt().label(),
        ac -> ac.court().type(),
        ac -> ac.court().location(),
        ActiveCitation::decisionDate,
        ActiveCitation::fileNumber,
        ActiveCitation::documentNumber
      )
      .containsExactly(
        "Änderung",
        "Änderung",
        "AG",
        "Aachen",
        "2021-10-20",
        "C-01/02",
        "WBRE000001234"
      );
  }

  @Test
  void convertToBusinessModel_activeReferences() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(
        AdmDocumentationUnitContent::activeReferences,
        InstanceOfAssertFactories.list(ActiveReference.class)
      )
      .hasSize(2)
      .extracting(
        ActiveReference::referenceDocumentType,
        ActiveReference::normAbbreviationRawValue,
        activeReference -> activeReference.verweisTyp().name(),
        activeReference ->
          activeReference.singleNorms().stream().map(SingleNorm::singleNorm).toList()
      )
      .containsExactly(
        tuple("administrative_regulation", "PhanGB", "Rechtsgrundlage", List.of("§ 1a Abs 1")),
        tuple("administrative_regulation", "PhanGB", "Rechtsgrundlage", List.of("§ 2 Abs 6"))
      );
  }

  @Test
  void convertToBusinessModel_normgeber() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(
        AdmDocumentationUnitContent::normgeberList,
        InstanceOfAssertFactories.list(Normgeber.class)
      )
      .hasSize(2)
      .extracting(
        normgeber -> normgeber.institution().name(),
        normgeber -> normgeber.institution().type(),
        normgeber -> normgeber.regions().stream().map(Region::code).toList()
      )
      .containsExactly(
        tuple("Erste Jurpn", InstitutionType.LEGAL_ENTITY, List.of()),
        tuple("Erstes Organ", InstitutionType.INSTITUTION, List.of("AA"))
      );
  }

  @Test
  @Tag("RISDEV-7936")
  void convertToBusinessModel_normgeberEqualNameForLegalEntityAndOrgan() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example-normgeber.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(
        AdmDocumentationUnitContent::normgeberList,
        InstanceOfAssertFactories.list(Normgeber.class)
      )
      .hasSize(2)
      .extracting(
        normgeber -> normgeber.institution().name(),
        normgeber -> normgeber.institution().type(),
        normgeber -> normgeber.regions().stream().map(Region::code).toList()
      )
      .containsExactly(
        tuple("JurpnOrgan", InstitutionType.LEGAL_ENTITY, List.of()),
        tuple("JurpnOrgan", InstitutionType.INSTITUTION, List.of("AA"))
      );
  }

  @Test
  void convertToBusinessModel_berufsbilder() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    List<String> expectedBerufsbilder = Stream.of("Brillenschleifer").toList();

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::berufsbilder)
      .isEqualTo(expectedBerufsbilder);
  }

  @Test
  void convertToBusinessModel_titelAspekte() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    List<String> expectedTitelAspekte = Stream.of("Gemeinsamer Bundesausschuss", "GBA").toList();

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::titelAspekte)
      .isEqualTo(expectedTitelAspekte);
  }

  @Test
  void convertToBusinessModel_definitionen() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR20250000001",
      UUID.randomUUID(),
      null,
      xml
    );

    List<Definition> expectedDefinitionen = Stream.of(new Definition("Sachgesamtheit")).toList();

    // when
    AdmDocumentationUnitContent admDocumentationUnitContent =
      ldmlToAdmConverterService.convertToBusinessModel(documentationUnit);

    // then
    assertThat(admDocumentationUnitContent)
      .isNotNull()
      .extracting(AdmDocumentationUnitContent::definitionen)
      .isEqualTo(expectedDefinitionen);
  }
}
