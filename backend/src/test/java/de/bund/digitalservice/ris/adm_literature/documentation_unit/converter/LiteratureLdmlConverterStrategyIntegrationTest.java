package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import static de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.XmlNormalizer.NORMALIZE_FUNCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.IDocumentationContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.TestDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.PublishingFailedException;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.XmlValidator;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LiteratureLdmlConverterStrategyIntegrationTest {

  @Autowired
  private LiteratureLdmlConverterStrategy literatureLdmlConverterStrategy;

  @Autowired
  @Qualifier("uliLiteratureValidator")
  private XmlValidator uliLiteratureValidator;

  @Autowired
  @Qualifier("sliLiteratureValidator")
  private XmlValidator sliLiteratureValidator;

  @Test
  void convertToLdml() {
    // given
    UliDocumentationUnitContent uliDocumentationUnitContent =
      TestDocumentationUnitContent.createUli("KSLU00000011", "2025");

    // when
    String xml = literatureLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

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
      TestDocumentationUnitContent.createUli("KSLU00000011", "2025");

    // when
    String xml = literatureLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

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
    String xml = literatureLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

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
    String xml = literatureLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

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
    String xml = literatureLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

    // then
    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <akn:FRBRalias name="dokumentarischerTitel" value="Dokumentarischer Titel"/>""".transform(
          NORMALIZE_FUNCTION
        )
    );
    assertThatCode(() -> uliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }

  @Test
  void convertToLdml_sli_complete() {
    // given
    SliDocumentationUnitContent sliDocumentationUnitContent = new SliDocumentationUnitContent(
      null,
      "KSLS00000022",
      "2024",
      Collections.emptyList(),
      "SliHauptTitel",
      null,
      null,
      "Dies ist eine Gesamtfussnote"
    );

    // when
    String xml = literatureLdmlConverterStrategy.convertToLdml(sliDocumentationUnitContent, null);

    // then
    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <akn:FRBRsubtype value="LS"/>""".transform(NORMALIZE_FUNCTION)
    );

    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <akn:notes source="gesamtfussnoten">
        <akn:note>
           <akn:block name="gesamtfussnote">Dies ist eine Gesamtfussnote</akn:block>
        </akn:note>
      </akn:notes>""".transform(NORMALIZE_FUNCTION)
    );

    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <akn:FRBRalias name="haupttitel" value="SliHauptTitel"/>""".transform(NORMALIZE_FUNCTION)
    );

    assertThatCode(() -> sliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }

  @Test
  void supports_shouldReturnTrueForSupportedTypes() {
    // given
    var uliContent = TestDocumentationUnitContent.createUli("DOC1", "2024");
    var sliContent = new SliDocumentationUnitContent(
      null,
      "DOC2",
      "2024",
      Collections.emptyList(),
      "Title",
      null,
      null,
      null
    );

    // then
    assertThat(literatureLdmlConverterStrategy.supports(uliContent)).isTrue();
    assertThat(literatureLdmlConverterStrategy.supports(sliContent)).isTrue();
  }

  @Test
  void supports_shouldReturnFalseForUnsupportedTypes() {
    // given
    IDocumentationContent unsupportedContent = Mockito.mock(IDocumentationContent.class);

    // then
    assertThat(literatureLdmlConverterStrategy.supports(unsupportedContent)).isFalse();
  }

  @Test
  void convertToLdml_shouldWrapExceptionsInPublishingFailedException() {
    // given
    IDocumentationContent unsupportedContent = Mockito.mock(IDocumentationContent.class);

    // when and then
    assertThatThrownBy(() -> literatureLdmlConverterStrategy.convertToLdml(unsupportedContent, null)
    )
      .isInstanceOf(PublishingFailedException.class)
      .hasMessageContaining("Failed to convert Literature content to LDML")
      .hasCauseInstanceOf(IllegalStateException.class)
      .hasRootCauseMessage("Unexpected content type: " + unsupportedContent.getClass());
  }
}
