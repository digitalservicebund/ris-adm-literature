package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungAdm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungRechtsprechung;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungSli;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.test.TestFile;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LdmlToSliConverterStrategyIntegrationTest {

  @Autowired
  private LdmlToSliConverterStrategy ldmlToSliConverterStrategy;

  @Test
  void convertToBusinessModel() {
    // given
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    UUID uuid = UUID.randomUUID();
    DocumentationUnit documentationUnit = new DocumentationUnit("KSLS20250000022", uuid, null, xml);

    // when
    SliDocumentationUnitContent sliDocumentationUnitContent =
      ldmlToSliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(sliDocumentationUnitContent)
      .isNotNull()
      .extracting(SliDocumentationUnitContent::id, SliDocumentationUnitContent::documentNumber)
      .containsExactly(uuid, "KSLS20250000022");
  }

  @Test
  void convertToBusinessModel_veroeffentlichungsJahr() {
    // given
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLS20250000022", xml);

    // when
    SliDocumentationUnitContent sliDocumentationUnitContent =
      ldmlToSliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(sliDocumentationUnitContent)
      .isNotNull()
      .extracting(SliDocumentationUnitContent::veroeffentlichungsjahr)
      .isEqualTo("2025");
  }

  @Test
  void convertToBusinessModel_documenttypen() {
    // given
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLS20250000022", xml);

    // when
    SliDocumentationUnitContent sliDocumentationUnitContent =
      ldmlToSliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(sliDocumentationUnitContent)
      .isNotNull()
      .extracting(SliDocumentationUnitContent::dokumenttypen)
      .asInstanceOf(InstanceOfAssertFactories.list(DocumentType.class))
      .extracting(DocumentType::abbreviation, DocumentType::name)
      .containsExactly(
        Tuple.tuple("Bib", "Bibliographie"),
        Tuple.tuple("Ebs", "Entscheidungsbesprechung")
      );
  }

  @Test
  void convertToBusinessModel_hauptsachtitel() {
    // given
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLS20250000022", xml);

    // when
    SliDocumentationUnitContent sliDocumentationUnitContent =
      ldmlToSliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(sliDocumentationUnitContent)
      .isNotNull()
      .extracting(
        SliDocumentationUnitContent::hauptsachtitel,
        SliDocumentationUnitContent::hauptsachtitelZusatz
      )
      .containsExactly("Lexikon der Spieltheorie", "Eine Einführung");
  }

  @Test
  void convertToBusinessModel_dokumentarischerTitel() {
    // given
    String xml = TestFile.readFileToString(
      "literature/sli/ldml-dokumentarischer-titel-example.akn.xml"
    );
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLS20250000033", xml);

    // when
    SliDocumentationUnitContent sliDocumentationUnitContent =
      ldmlToSliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(sliDocumentationUnitContent.dokumentarischerTitel()).isEqualTo(
      "Ein interessantes Buch"
    );
  }

  @Test
  void convertToBusinessModel_aktivzitierungSli() {
    // given
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLS20250000022", xml);

    // when
    SliDocumentationUnitContent sliDocumentationUnitContent =
      ldmlToSliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(sliDocumentationUnitContent)
      .isNotNull()
      .extracting(
        SliDocumentationUnitContent::aktivzitierungenSli,
        InstanceOfAssertFactories.list(AktivzitierungSli.class)
      )
      .hasSize(2)
      .extracting(
        AktivzitierungSli::titel,
        AktivzitierungSli::verfasser,
        AktivzitierungSli::veroeffentlichungsJahr,
        AktivzitierungSli::isbn,
        AktivzitierungSli::dokumenttypen,
        AktivzitierungSli::documentNumber
      )
      .containsExactly(
        Tuple.tuple(
          "Gesamtplan für ein kooperatives System der Erwachsenenbildung",
          List.of("Arbeitskreis Erwachsenenbildung"),
          "1968",
          null,
          null,
          null
        ),
        Tuple.tuple(
          "Die Grenzen des Lebens",
          List.of("Dworkin, Ronald"),
          "1994",
          null,
          null,
          "KSLS015481422"
        )
      );
  }

  @Test
  void convertToBusinessModel_aktivzitierungAdm() {
    // given
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLS20250000022", xml);

    // when
    SliDocumentationUnitContent sliDocumentationUnitContent =
      ldmlToSliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(sliDocumentationUnitContent)
      .isNotNull()
      .extracting(
        SliDocumentationUnitContent::aktivzitierungenAdm,
        InstanceOfAssertFactories.list(AktivzitierungAdm.class)
      )
      .hasSize(2)
      .extracting(
        AktivzitierungAdm::citationType,
        AktivzitierungAdm::periodikum,
        AktivzitierungAdm::zitatstelle,
        AktivzitierungAdm::inkrafttretedatum,
        AktivzitierungAdm::dokumenttyp,
        AktivzitierungAdm::aktenzeichen,
        AktivzitierungAdm::normgeber,
        AktivzitierungAdm::documentNumber
      )
      .containsExactly(
        Tuple.tuple(
          "Übernahme",
          "ABc",
          "2025, Seite 52",
          "2025-01-01",
          "VR",
          null,
          "Erstes Organ",
          "KSNR202500000009"
        ),
        Tuple.tuple("Übernahme", null, null, "2025-12-01", "VV", null, "Zweites Organ", null)
      );
  }

  @Test
  void convertToBusinessModel_aktivzitierungRechtsprechung() {
    // given
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLS20250000022", xml);

    // when
    SliDocumentationUnitContent sliDocumentationUnitContent =
      ldmlToSliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(sliDocumentationUnitContent)
      .isNotNull()
      .extracting(
        SliDocumentationUnitContent::aktivzitierungenRechtsprechung,
        InstanceOfAssertFactories.list(AktivzitierungRechtsprechung.class)
      )
      .hasSize(1)
      .extracting(
        AktivzitierungRechtsprechung::citationType,
        AktivzitierungRechtsprechung::entscheidungsdatum,
        AktivzitierungRechtsprechung::dokumenttyp,
        AktivzitierungRechtsprechung::aktenzeichen,
        AktivzitierungRechtsprechung::gerichttyp,
        AktivzitierungRechtsprechung::gerichtort,
        AktivzitierungRechtsprechung::documentNumber
      )
      .containsExactly(
        Tuple.tuple(
          "Vergleiche",
          "1988-11-09",
          "Vgl",
          "5 Sa 292/88",
          "LArbG",
          "München",
          "KARE339410237"
        )
      );
  }

  private static DocumentationUnit createDocumentationUnit(String documentNumber, String xml) {
    return new DocumentationUnit(documentNumber, UUID.randomUUID(), null, xml, null);
  }
}
