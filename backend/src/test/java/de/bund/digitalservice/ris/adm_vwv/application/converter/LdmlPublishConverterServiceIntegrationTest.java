package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.TestDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.TestNormgeber;
import de.bund.digitalservice.ris.adm_vwv.test.TestFile;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LdmlPublishConverterServiceIntegrationTest {

  @Autowired
  private LdmlPublishConverterService ldmlPublishConverterService;

  @Test
  @DisplayName("Conversion of document with document number results into xml with FRBRalias")
  void convertToLdml() {
    // given
    DocumentationUnitContent documentationUnitContent = TestDocumentationUnitContent.create(
      "KSNR00000011",
      "Lange Überschrift"
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:FRBRalias name="documentNumber" value="KSNR00000011"/>
      """
    );
  }

  @Test
  @DisplayName(
    "Conversion of document with document number and an existing published version " +
    "results into xml with FRBRalias"
  )
  void convertToLdml_withExistingVersion() {
    // given
    String previousXmlVersion = TestFile.readFileToString("ldml-example.akn.xml");
    DocumentationUnitContent documentationUnitContent = TestDocumentationUnitContent.create(
      "KSNR00000011",
      "Lange Überschrift"
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(
      documentationUnitContent,
      previousXmlVersion
    );

    // then
    assertThat(xml).contains(
      """
      <akn:FRBRalias name="documentNumber" value="KSNR00000011"/>
      """
    );
  }

  @Test
  @DisplayName(
    "Converts Inkrafttretedatum, Ausserkrafttretedatum and Zitierdaten to tags in ris:metadata"
  )
  void convertToLdml_dates() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of("2025-02-04", "2025-02-05"),
      "2025-01-01",
      "2027-09-30",
      null,
      null,
      List.of(),
      true,
      null,
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of()
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <ris:entryIntoEffectDate>2025-01-01</ris:entryIntoEffectDate>
      <ris:expiryDate>2027-09-30</ris:expiryDate>
      <ris:dateToQuoteList>
          <ris:dateToQuoteEntry>2025-02-04</ris:dateToQuoteEntry>
          <ris:dateToQuoteEntry>2025-02-05</ris:dateToQuoteEntry>
      </ris:dateToQuoteList>""".indent(20)
    );
  }

  @Test
  @DisplayName("Converts 'Langüberschrift' to akn:longTitle in akn:preface")
  void convertToLdml_langueberschrift() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of(),
      null,
      null,
      null,
      null,
      List.of(),
      true,
      null,
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of()
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:preface>
          <akn:longTitle>
              <akn:block name="longTitle">Lange Überschrift</akn:block>
          </akn:longTitle>
      </akn:preface>""".indent(8)
    );
  }

  @Test
  @DisplayName("Converts 'Gliederung' to ris:tableOfContentEntries")
  void convertToLdml_gliederung() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of(),
      null,
      null,
      """
      <p>Erste Zeile</p>
      <p>Zweite Zeile</p>
      """,
      null,
      List.of(),
      true,
      null,
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of()
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <ris:tableOfContentsEntries>
          <ris:tableOfContentsEntry>Erste Zeile</ris:tableOfContentsEntry>
          <ris:tableOfContentsEntry>Zweite Zeile</ris:tableOfContentsEntry>
      </ris:tableOfContentsEntries>""".indent(20)
    );
  }

  @Test
  @DisplayName("Converts 'Kurzreferat' to akn:div in akn:mainBody and transforms p to akn:p")
  void convertToLdml_kurzreferat() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of(),
      null,
      null,
      null,
      """
      <p>Erste Zeile</p>
      <p></p>
      <p>Zweite Zeile</p>""",
      List.of(),
      true,
      null,
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of()
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:mainBody>
          <akn:div>
              <akn:p>Erste Zeile</akn:p>
              <akn:p/>
              <akn:p>Zweite Zeile</akn:p>
          </akn:div>
      </akn:mainBody>""".indent(8)
    );
  }

  @Test
  @DisplayName("Converts an empty 'Kurzreferat' to akn:hcontainer in akn:mainBody")
  void convertToLdml_emptyKurzreferat() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of(),
      null,
      null,
      null,
      "  ",
      List.of(),
      true,
      null,
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of()
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:mainBody>
          <akn:hcontainer name="crossheading"/>
      </akn:mainBody>""".indent(8)
    );
  }

  @Test
  @DisplayName("Converts two 'Normgeber' to two ris:normgeber in ris:metadata")
  void convertToLdml_normgeber() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of(),
      null,
      null,
      null,
      "  ",
      List.of(),
      true,
      null,
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(
        TestNormgeber.createByLegalEntity("Bundesagentur"),
        TestNormgeber.createByInstitution("Bundesregierung", "DEU")
      )
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <ris:metadata>
          <ris:normgeber staat="Bundesagentur"/>
          <ris:normgeber staat="DEU" organ="Bundesregierung"/>
      </ris:metadata>""".indent(16)
    );
  }
}
