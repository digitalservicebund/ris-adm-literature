package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import static de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.XmlNormalizer.NORMALIZE_FUNCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.TestUliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.XmlValidator;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UliLdmlConverterStrategyIntegrationTest {

  @Autowired
  private UliLdmlConverterStrategy uliLdmlConverterStrategy;

  @Autowired
  @Qualifier("uliLiteratureValidator")
  private XmlValidator uliLiteratureValidator;

  @Test
  void convertToLdml() {
    // given
    UliDocumentationUnitContent uliDocumentationUnitContent =
      TestUliDocumentationUnitContent.create("KSLU00000011", "2025");

    // when
    String xml = uliLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:FRBRalias name="Dokumentnummer" value="KSLU00000011"/>"""
    );
    assertThatCode(() -> uliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }

  @Test
  void convertToLdml_veroeffentlichungsjahr() {
    // given
    UliDocumentationUnitContent uliDocumentationUnitContent =
      TestUliDocumentationUnitContent.create("KSLU00000011", "2025");

    // when
    String xml = uliLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

    // then
    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <ris:veroeffentlichungsJahre>
        <ris:veroeffentlichungsJahr>2025</ris:veroeffentlichungsJahr>
      </ris:veroeffentlichungsJahre>""".transform(NORMALIZE_FUNCTION)
    );
    assertThatCode(() -> uliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }

  @Test
  void convertToLdml_documentType() {
    // given
    UliDocumentationUnitContent uliDocumentationUnitContent = new UliDocumentationUnitContent(
      null,
      "KSLU00000011",
      "2025",
      List.of(
        new DocumentType("Auf", "Aufsatz"),
        new DocumentType("Ebs", "Entscheidungsbesprechung")
      ),
      null,
      null,
      null,
      null
    );

    // when
    String xml = uliLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

    // then
    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <akn:classification source="doktyp">
        <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Auf" value="Auf"/>
        <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Ebs" value="Ebs"/>
      </akn:classification>""".transform(NORMALIZE_FUNCTION)
    );
    assertThatCode(() -> uliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }

  @Test
  void convertToLdml_haupttitel() {
    // given
    UliDocumentationUnitContent uliDocumentationUnitContent = new UliDocumentationUnitContent(
      null,
      "KSLU00000011",
      "2025",
      List.of(new DocumentType("Auf", "Aufsatz")),
      "Haupt",
      "Zusatz",
      null,
      null
    );

    // when
    String xml = uliLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

    // then
    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <akn:FRBRalias name="haupttitel" value="Haupt"/>
      <akn:FRBRalias name="haupttitelZusatz" value="Zusatz"/>""".transform(NORMALIZE_FUNCTION)
    );
    assertThatCode(() -> uliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }

  @Test
  void convertToLdml_dokumentarischerTitel() {
    // given
    UliDocumentationUnitContent uliDocumentationUnitContent = new UliDocumentationUnitContent(
      null,
      "KSLU00000011",
      "2025",
      List.of(new DocumentType("Auf", "Aufsatz")),
      null,
      null,
      "Dokumentarischer Titel",
      null
    );

    // when
    String xml = uliLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

    // then
    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <akn:FRBRalias name="dokumentarischerTitel" value="Dokumentarischer Titel"/>""".transform(
          NORMALIZE_FUNCTION
        )
    );
    assertThatCode(() -> uliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }
}
