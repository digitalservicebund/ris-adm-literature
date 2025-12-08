package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.converter;

import static de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.XmlNormalizer.NORMALIZE_FUNCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.TestLiteratureUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.PublishingFailedException;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.XmlValidator;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
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
    UliDocumentationUnitContent uliDocumentationUnitContent = TestLiteratureUnitContent.createUli(
      "KSLU00000011",
      "2025"
    );

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
    UliDocumentationUnitContent uliDocumentationUnitContent = TestLiteratureUnitContent.createUli(
      "KSLU00000011",
      "2025"
    );

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
      "Dies ist eine Gesamtfussnote",
      null
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
    var uliContent = TestLiteratureUnitContent.createUli("DOC1", "2024");
    var sliContent = TestLiteratureUnitContent.createSli("DOC2", "2024");

    // then
    assertThat(literatureLdmlConverterStrategy.supports(uliContent)).isTrue();
    assertThat(literatureLdmlConverterStrategy.supports(sliContent)).isTrue();
  }

  @Test
  void supports_shouldReturnFalseForUnsupportedTypes() {
    // given
    DocumentationUnitContent unsupportedContent = Mockito.mock(DocumentationUnitContent.class);

    // then
    assertThat(literatureLdmlConverterStrategy.supports(unsupportedContent)).isFalse();
  }

  @Test
  void convertToLdml_shouldWrapExceptionsInPublishingFailedException() {
    // given
    DocumentationUnitContent unsupportedContent = Mockito.mock(AdmDocumentationUnitContent.class);

    // when and then
    assertThatThrownBy(() -> literatureLdmlConverterStrategy.convertToLdml(unsupportedContent, null)
    )
      .isInstanceOf(PublishingFailedException.class)
      .hasMessageContaining("Failed to convert Literature content to LDML")
      .hasCauseInstanceOf(IllegalStateException.class)
      .hasRootCauseMessage("Unexpected content type: " + unsupportedContent.getClass());
  }

  @Test
  @DisplayName(
    "Elements of <aktivsli> are processed into a <ris:selbstaendigeLiteraturReference> in <akn:implicitReference> in <akn:otherReferences>"
  )
  void process_aktivzitierungSelbstaendigeLiteratur() {
    // given
    SliDocumentationUnitContent sliDocumentationUnitContent = new SliDocumentationUnitContent(
      null,
      "KSLS00000022",
      "2024",
      Collections.emptyList(),
      "SliHauptTitel",
      null,
      null,
      "Dies ist eine Gesamtfussnote",
      List.of(
        new SliDocumentationUnitContent.AktivzitierungSli(
          "docnum",
          "jahr",
          "titel",
          "isbn",
          List.of("autor"),
          List.of(new DocumentType("VR", "Verwaltungsregelung"))
        )
      )
    );

    // when
    String xml = literatureLdmlConverterStrategy.convertToLdml(sliDocumentationUnitContent, null);

    // then
    assertThat(xml.transform(NORMALIZE_FUNCTION)).contains(
      """
      <akn:analysis source="attributsemantik-noch-undefiniert">
        <akn:otherReferences source="active">
          <akn:implicitReference showAs="titel, jahr, autor">
            <ris:selbstaendigeLiteraturReference autor="autor" buchtitel="titel" documentNumber="docnum" dokumentTyp="VR" isbn="isbn" veroeffentlichungsJahr="jahr"/>
          </akn:implicitReference>
        </akn:otherReferences>
      </akn:analysis>""".transform(NORMALIZE_FUNCTION)
    );
    assertThatCode(() -> sliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("When previousXmlVersion is provided, it is ignored and a new document is created")
  void convertToLdml_withPreviousXmlVersion_shouldIgnoreAndCreateNew() {
    // given
    UliDocumentationUnitContent uliDocumentationUnitContent =
      TestDocumentationUnitContent.createUli("KSLU00000099", "2025");
    String previousXmlVersion = "<previous>valid or invalid xml content</previous>";

    // when
    String xml = literatureLdmlConverterStrategy.convertToLdml(
      uliDocumentationUnitContent,
      previousXmlVersion
    );

    // then
    assertThat(xml).contains(
      """
      <akn:FRBRalias name="Dokumentnummer" value="KSLU00000099"/>"""
    );

    assertThatCode(() -> uliLiteratureValidator.validate(xml)).doesNotThrowAnyException();
  }
}
