package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import de.bund.digitalservice.ris.adm_vwv.application.Fundstelle;
import de.bund.digitalservice.ris.adm_vwv.application.LegalPeriodical;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.*;
import de.bund.digitalservice.ris.adm_vwv.test.TestFile;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <ris:entryIntoEffectDate>2025-01-01</ris:entryIntoEffectDate>
      <ris:expiryDate>2027-09-30</ris:expiryDate>""".indent(20),
      """
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
      List.of("2025-01-01"),
      null,
      null,
      null,
      null,
      List.of(),
      true,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
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
      List.of("2025-01-01"),
      null,
      null,
      """
      <p>Erste Zeile</p>
      <p>Zweite Zeile</p>
      """,
      null,
      List.of(),
      true,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
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
      List.of("2025-01-01"),
      null,
      null,
      null,
      """
      <p>Erste Zeile</p>
      <p></p>
      <p>Zweite Zeile</p>""",
      List.of(),
      true,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
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
      List.of("2025-01-01"),
      null,
      null,
      null,
      "  ",
      List.of(),
      true,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
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
      List.of("2025-01-01"),
      null,
      null,
      null,
      "  ",
      List.of(),
      true,
      new DocumentType("VR", "Verwaltungsregelung"),
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
      <ris:normgeber staat="Bundesagentur"/>
      <ris:normgeber staat="DEU" organ="Bundesregierung"/>""".indent(20)
    );
  }

  @Test
  @DisplayName("Converts three keywords to three akn:keyword in akn:classification")
  void convertToLdml_keywords() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of("Schlag", "Wort", "Langer Text mit Sonderzeichen <>& und Leerzeichen"),
      List.of("2025-01-01"),
      null,
      null,
      null,
      "  ",
      List.of(),
      true,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:classification source="attributsemantik-noch-undefiniert">
          <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Schlag" value="Schlag"/>
          <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Wort" value="Wort"/>
          <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Langer Text mit Sonderzeichen &lt;&gt;&amp; und Leerzeichen" value="Langer Text mit Sonderzeichen &lt;&gt;&amp; und Leerzeichen"/>
      </akn:classification>""".indent(12)
    );
  }

  @Test
  @DisplayName(
    "Converts 'Sachgebiete', 'Dokumenttyp', 'Dokumenttypzusatz', and 'Aktenzeichen' into ris:metadata"
  )
  void convertToLdml_classifications() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(
        FieldOfLaw.builder().identifier("SO-01").notation("NEW").build(),
        FieldOfLaw.builder().identifier("03-04").notation("OLD").build()
      ),
      "Lange Überschrift",
      List.of(),
      List.of("2025-01-01"),
      null,
      null,
      null,
      "  ",
      List.of("X/I-43", "Akt. 45 / 2-3"),
      false,
      new DocumentType("VR", "Verwaltungsregelung"),
      "Zusatz",
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <ris:fieldsOfLaw>
          <ris:fieldOfLaw notation="NEW">SO-01</ris:fieldOfLaw>
          <ris:fieldOfLaw notation="OLD">03-04</ris:fieldOfLaw>
      </ris:fieldsOfLaw>
      <ris:documentType category="VR" longTitle="Zusatz">VR Zusatz</ris:documentType>""".indent(20),
      """
      <ris:referenceNumbers>
          <ris:referenceNumber>X/I-43</ris:referenceNumber>
          <ris:referenceNumber>Akt. 45 / 2-3</ris:referenceNumber>
      </ris:referenceNumbers>""".indent(20)
    );
  }

  @Test
  @DisplayName(
    "Converts 'Dokumenttyp' without 'Dokumenttypzusatz' to ris:documentType in ris:metadata"
  )
  void convertToLdml_noDokumenttypZusatz() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of("2025-01-01"),
      null,
      null,
      null,
      "  ",
      List.of(),
      false,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <ris:documentType category="VR">VR</ris:documentType>""".indent(20)
    );
  }

  @Test
  @DisplayName(
    "Converts 'Fundstelle', 'Normenkette', and 'Aktivzitierung Rechtssprechung' into akn:analysis"
  )
  void convertToLdml_references() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(
        new Fundstelle(
          UUID.randomUUID(),
          "Seite 2",
          new LegalPeriodical(UUID.randomUUID(), "DOK", "Die Dokumente", null, null),
          null
        ),
        new Fundstelle(
          UUID.randomUUID(),
          "Kap. 5, Abs. 1",
          new LegalPeriodical(UUID.randomUUID(), "VJP", "Juristische Periodika", null, null),
          null
        )
      ),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of("2025-01-01"),
      null,
      null,
      null,
      "  ",
      List.of(),
      false,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(
        new ActiveCitation(
          UUID.randomUUID(),
          false,
          "KSNR00000011",
          new Court(UUID.randomUUID(), "sozial", "Kassel", "BSG"),
          "2024-02-04",
          "X/I 43",
          null,
          new CitationType(UUID.randomUUID(), "Ueb", "Übernahme")
        )
      ),
      List.of(),
      List.of(
        new NormReference(
          new NormAbbreviation(UUID.randomUUID(), "BGB", "Bürgerliches Gesetzbuch"),
          "BGB",
          List.of(
            new SingleNorm(UUID.randomUUID(), "§1", null, null),
            new SingleNorm(UUID.randomUUID(), "§2 Abs. 1", "2025-01-01", "2025")
          )
        )
      ),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:analysis source="attributsemantik-noch-undefiniert">
          <akn:otherReferences source="attributsemantik-noch-undefiniert">
              <akn:implicitReference shortForm="DOK" showAs="DOK, Seite 2"/>
              <akn:implicitReference shortForm="VJP" showAs="VJP, Kap. 5, Abs. 1"/>
              <akn:implicitReference shortForm="BGB" showAs="BGB §1">
                  <ris:normReference singleNorm="§1"/>
              </akn:implicitReference>
              <akn:implicitReference shortForm="BGB" showAs="BGB §2 Abs. 1">
                  <ris:normReference singleNorm="§2 Abs. 1" dateOfRelevance="2025" dateOfVersion="2025-01-01"/>
              </akn:implicitReference>
              <akn:implicitReference shortForm="Übernahme BSG X/I 43" showAs="Übernahme BSG X/I 43 2024-02-04">
                  <ris:caselawReference abbreviation="Übernahme" court="BSG" courtLocation="Kassel" date="2024-02-04" documentNumber="KSNR00000011" referenceNumber="X/I 43"/>
              </akn:implicitReference>
          </akn:otherReferences>
      </akn:analysis>""".indent(12)
    );
  }

  @Test
  @DisplayName("Converts 'Fundstelle' with two ambiguous periodika into akn:analysis")
  void convertToLdml_fundstelleWithAmbiguousPeriodikum() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(
        new Fundstelle(UUID.randomUUID(), "Seite 2", null, "DOK"),
        new Fundstelle(UUID.randomUUID(), "Kap. 5, Abs. 1", null, "VJP")
      ),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of(),
      null,
      null,
      null,
      "  ",
      List.of(),
      false,
      new DocumentType("VR", "Verwaltungsregelung"),
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
      <akn:analysis source="attributsemantik-noch-undefiniert">
          <akn:otherReferences source="attributsemantik-noch-undefiniert">
              <akn:implicitReference shortForm="DOK" showAs="DOK, Seite 2"/>
              <akn:implicitReference shortForm="VJP" showAs="VJP, Kap. 5, Abs. 1"/>
          </akn:otherReferences>
      </akn:analysis>""".indent(12)
    );
  }

  @Test
  @DisplayName("Converts 'Aktivverweisungen' into ris:activeReferences in ris:metadata")
  void convertToLdml_activeReferences() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR00000011",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of("2025-01-01"),
      null,
      null,
      null,
      "  ",
      List.of(),
      false,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(
        new ActiveReference(
          "administrative_regulation",
          "anwendung",
          new NormAbbreviation(UUID.randomUUID(), "VR XML-Erzeugung", null),
          null,
          List.of(
            new SingleNorm(UUID.randomUUID(), "§ 2 Seite 1", "2025-01-01", null),
            new SingleNorm(UUID.randomUUID(), "§ 5", "2025-01-02", null)
          )
        ),
        new ActiveReference(
          "norm",
          "rechtsgrundlage",
          new NormAbbreviation(UUID.randomUUID(), "BGB", null),
          null,
          List.of(
            new SingleNorm(UUID.randomUUID(), "§ 10 Abs. 1", null, "2010"),
            new SingleNorm(UUID.randomUUID(), "§ 11", "2001-02-05", null)
          )
        ),
        new ActiveReference(
          "administrative_regulation",
          "neuregelung",
          new NormAbbreviation(UUID.randomUUID(), "VV RIS-Abkürzungen", null),
          null,
          List.of()
        )
      ),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <ris:activeReferences>
          <ris:activeReference typeNumber="01" reference="VR XML-Erzeugung" paragraph="§ 2 Seite 1" dateOfVersion="2025-01-01"/>
          <ris:activeReference typeNumber="01" reference="VR XML-Erzeugung" paragraph="§ 5" dateOfVersion="2025-01-02"/>
          <ris:activeReference typeNumber="82" reference="BGB" paragraph="§ 10 Abs. 1"/>
          <ris:activeReference typeNumber="82" reference="BGB" paragraph="§ 11" dateOfVersion="2001-02-05"/>
          <ris:activeReference typeNumber="31" reference="VV RIS-Abkürzungen"/>
      </ris:activeReferences>""".indent(20)
    );
  }

  @Test
  @DisplayName(
    "Keeps 'Zuordnungen', 'Verwaltungsdaten', 'Juris-Abkürzung', and 'Region' from previous document version in ris:metadata"
  )
  void convertToLdml_previousVersionContent() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR1234567890",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of("2025-01-01"),
      null,
      null,
      null,
      null,
      List.of(),
      false,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("DEU"))
    );
    String previousXml = TestFile.readFileToString("ldml-example-historic-data.akn.xml");

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, previousXml);

    // then
    assertThat(xml).contains(
      """
      <ris:zuordnungen>
          <ris:zuordnung aspekt="Titel" begriff="Bundesausschuss"/>
          <ris:zuordnung aspekt="Titel" begriff="BA"/>
          <ris:zuordnung aspekt="VRNr" begriff="12345"/>
      </ris:zuordnungen>""".indent(20),
      """
      <ris:historicAdministrativeData>
          <dokstelle name="BSG" status="1">
              <fantasiedatum>BSG</fantasiedatum>
              <erzeugt>2025-01-01</erzeugt>
              <geaendert>2025-01-01 14:06</geaendert>
              <publiziert>
                  <datum>2025-01-02 00:00</datum>
                  <file>BSG.VR.20250101</file>
                  <status>5</status>
              </publiziert>
          </dokstelle>
      </ris:historicAdministrativeData>""".indent(20),
      """
      <ris:region>DEU</ris:region>
      <ris:historicAbbreviation>VR Bundesausschuss 2025-01-01 12345</ris:historicAbbreviation>""".indent(
          20
        )
    );
  }

  @Test
  @DisplayName("Convert ldml identification in akn:meta")
  void convertToLdml_identification() {
    // given
    DocumentationUnitContent documentationUnitContent = new DocumentationUnitContent(
      null,
      "KSNR1234567890",
      List.of(),
      List.of(),
      "Lange Überschrift",
      List.of(),
      List.of("2025-02-02"),
      null,
      null,
      null,
      null,
      List.of("X 43/I 9"),
      false,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("Bundesausschuss ÜT 1"))
    );

    // when
    String xml = ldmlPublishConverterService.convertToLdml(documentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:identification source="attributsemantik-noch-undefiniert">
          <akn:FRBRWork>
              <akn:FRBRthis value="/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet-1/2025/x-43-i-9"/>
              <akn:FRBRuri value="/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet-1/2025/x-43-i-9"/>
              <akn:FRBRdate date="${generierung}" name="erfassungsdatum"/>
              <akn:FRBRauthor href="recht.bund.de/institution/bundessozialgericht"/>
              <akn:FRBRcountry value="de"/>
              <akn:FRBRsubtype value="VR"/>
              <akn:FRBRnumber value="X 43/I 9"/>
              <akn:FRBRname value="Bundesausschuss ÜT 1"/>
          </akn:FRBRWork>
          <akn:FRBRExpression>
              <akn:FRBRthis value="/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet-1/2025/x-43-i-9/2025-02-02/deu"/>
              <akn:FRBRuri value="/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet-1/2025/x-43-i-9/2025-02-02/deu"/>
              <akn:FRBRalias name="documentNumber" value="KSNR1234567890"/>
              <akn:FRBRdate date="2025-02-02" name="zitierdatum"/>
              <akn:FRBRauthor href="recht.bund.de/institution/bundessozialgericht"/>
              <akn:FRBRlanguage language="deu"/>
          </akn:FRBRExpression>
          <akn:FRBRManifestation>
              <akn:FRBRthis value="/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet-1/2025/x-43-i-9/2025-02-02/deu.akn.xml"/>
              <akn:FRBRuri value="/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet-1/2025/x-43-i-9/2025-02-02/deu.akn.xml"/>
              <akn:FRBRdate date="${generierung}" name="generierung"/>
              <akn:FRBRauthor href="recht.bund.de/institution/bundessozialgericht"/>
              <akn:FRBRformat value="xml"/>
          </akn:FRBRManifestation>
      </akn:identification>
      """.replace("${generierung}", LocalDate.now().toString()).indent(12)
    );
  }
}
