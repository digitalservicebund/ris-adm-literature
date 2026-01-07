package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.test.TestFile;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LdmlToUliConverterStrategyIntegrationTest {

  @Autowired
  private LdmlToUliConverterStrategy ldmlToUliConverterStrategy;

  @Test
  void convertToBusinessModel() {
    // given
    String xml = TestFile.readFileToString("literature/uli/ldml-example.akn.xml");
    UUID uuid = UUID.randomUUID();
    DocumentationUnit documentationUnit = new DocumentationUnit("KSLU20250000022", uuid, null, xml);

    // when
    UliDocumentationUnitContent uliDocumentationUnitContent =
      ldmlToUliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(uliDocumentationUnitContent)
      .isNotNull()
      .extracting(UliDocumentationUnitContent::id, UliDocumentationUnitContent::documentNumber)
      .containsExactly(uuid, "KSLU20250000022");
  }

  @Test
  void convertToBusinessModel_veroeffentlichungsJahr() {
    // given
    String xml = TestFile.readFileToString("literature/uli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLU20250000022", xml);

    // when
    UliDocumentationUnitContent uliDocumentationUnitContent =
      ldmlToUliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(uliDocumentationUnitContent)
      .isNotNull()
      .extracting(UliDocumentationUnitContent::veroeffentlichungsjahr)
      .isEqualTo("2025");
  }

  @Test
  void convertToBusinessModel_documenttypen() {
    // given
    String xml = TestFile.readFileToString("literature/uli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLU20250000022", xml);

    // when
    UliDocumentationUnitContent uliDocumentationUnitContent =
      ldmlToUliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(uliDocumentationUnitContent)
      .isNotNull()
      .extracting(UliDocumentationUnitContent::dokumenttypen)
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
    String xml = TestFile.readFileToString("literature/uli/ldml-example.akn.xml");
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLU20250000022", xml);

    // when
    UliDocumentationUnitContent uliDocumentationUnitContent =
      ldmlToUliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(uliDocumentationUnitContent)
      .isNotNull()
      .extracting(
        UliDocumentationUnitContent::hauptsachtitel,
        UliDocumentationUnitContent::hauptsachtitelZusatz
      )
      .containsExactly("Lexikon der Spieltheorie", "Eine Einführung");
  }

  @Test
  void convertToBusinessModel_dokumentarischerTitel() {
    // given
    String xml = TestFile.readFileToString(
      "literature/uli/ldml-dokumentarischer-titel-example.akn.xml"
    );
    DocumentationUnit documentationUnit = createDocumentationUnit("KSLU20250000033", xml);

    // when
    UliDocumentationUnitContent uliDocumentationUnitContent =
      ldmlToUliConverterStrategy.convertToBusinessModel(documentationUnit);

    // then
    assertThat(uliDocumentationUnitContent.dokumentarischerTitel()).isEqualTo(
      "Ein interessantes Buch"
    );
  }

  private static DocumentationUnit createDocumentationUnit(String documentNumber, String xml) {
    return new DocumentationUnit(documentNumber, UUID.randomUUID(), null, xml, null);
  }
}
