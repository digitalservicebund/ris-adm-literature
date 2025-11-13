package de.bund.digitalservice.ris.adm_vwv.application.converter.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class XmlAppenderTest {

  private static final String TEST_LDML =
    """
    <?xml version="1.0" encoding="UTF-8"?>
    <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0" xmlns:ris="http://ldml.neuris.de/meta/">
        <akn:doc name="offene-struktur">
            <akn:meta>
            </akn:meta>
        </akn:doc>
    </akn:akomaNtoso>""";

  @Test
  void addAttributeToNode() throws Exception {
    // given
    Document document = createDocument();

    // when
    XmlAppender.addAttributeToNode(document.getElementsByTagName("akn:doc").item(0), "xml", "on");

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).contains(
      """
      <akn:doc name="offene-struktur" xml="on">"""
    );
  }

  @Test
  @DisplayName("Insert an element into an empty meta element")
  void insertOrderedElement() throws Exception {
    // given
    Document document = createDocument();
    Element metaNode = (Element) document.getElementsByTagName("akn:meta").item(0);
    Element referencesNode = document.createElement("akn:references");

    // when
    XmlAppender.insertOrderedElement(metaNode, referencesNode);

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).containsIgnoringWhitespaces(
      """
      <akn:meta>
        <akn:references/>
      </akn:meta>"""
    );
  }

  @Test
  @DisplayName("Insert classification element before references element")
  void insertOrderedElement_insertBefore() throws Exception {
    // given
    Document document = createDocument();
    Element metaElement = (Element) document.getElementsByTagName("akn:meta").item(0);
    metaElement.appendChild(document.createElement("akn:references"));
    metaElement.appendChild(document.createElement("akn:notes"));
    Element classificationElement = document.createElement("akn:classification");

    // when
    XmlAppender.insertOrderedElement(metaElement, classificationElement);

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).containsIgnoringWhitespaces(
      """
      <akn:meta>
        <akn:classification/>
        <akn:references/>
        <akn:notes/>
      </akn:meta>"""
    );
  }

  @Test
  @DisplayName("Insert references element after classification element")
  void insertOrderedElement_insertAfter() throws Exception {
    // given
    Document document = createDocument();
    Element metaElement = (Element) document.getElementsByTagName("akn:meta").item(0);
    metaElement.appendChild(document.createElement("akn:classification"));
    Element referencesElement = document.createElement("akn:references");

    // when
    XmlAppender.insertOrderedElement(metaElement, referencesElement);

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).containsIgnoringWhitespaces(
      """
      <akn:meta>
        <akn:classification/>
        <akn:references/>
      </akn:meta>"""
    );
  }

  @Test
  @DisplayName("Insert unknown fantasy element as last child of meta element")
  void insertOrderedElement_insertUnknown() throws Exception {
    // given
    Document document = createDocument();
    Element metaElement = (Element) document.getElementsByTagName("akn:meta").item(0);
    metaElement.appendChild(document.createElement("akn:classification"));
    Element fantasyElement = document.createElement("akn:fantasy");

    // when
    XmlAppender.insertOrderedElement(metaElement, fantasyElement);

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).containsIgnoringWhitespaces(
      """
      <akn:meta>
        <akn:classification/>
        <akn:fantasy/>
      </akn:meta>"""
    );
  }

  @Test
  @DisplayName("Insert 4th classification element after the 3th classification element")
  void insertOrderedElement_insertMultipleTimes() throws Exception {
    // given
    Document document = createDocument();
    Element metaNode = (Element) document.getElementsByTagName("akn:meta").item(0);
    metaNode.appendChild(document.createElement("akn:identification"));
    metaNode.appendChild(document.createElement("akn:classification"));
    metaNode.appendChild(document.createElement("akn:classification"));
    metaNode.appendChild(document.createElement("akn:references"));
    Element classificationElement = document.createElement("akn:classification");
    classificationElement.setAttribute("ris:titel", "test");

    // when
    XmlAppender.insertOrderedElement(metaNode, classificationElement);

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).containsIgnoringWhitespaces(
      """
      <akn:meta>
        <akn:identification/>
        <akn:classification/>
        <akn:classification/>
        <akn:classification ris:titel="test"/>
        <akn:references/>
      </akn:meta>"""
    );
  }

  @Test
  @DisplayName("Insert new classification element")
  void insertOrderedElementIfNotExist_elementNotExisting() throws Exception {
    // given
    Document document = createDocument();
    Element metaNode = (Element) document.getElementsByTagName("akn:meta").item(0);
    Element classificationElement = document.createElement("akn:classification");
    classificationElement.setAttribute("ris:titel", "test");

    // when
    XmlAppender.insertOrderedElementIfNotExists(metaNode, classificationElement);

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).containsIgnoringWhitespaces(
      """
      <akn:meta>
        <akn:classification ris:titel="test"/>
      </akn:meta>"""
    );
  }

  @Test
  @DisplayName(
    "Insert new classification element although there is an existing with different attribute list"
  )
  void insertOrderedElementIfNotExist_elementExistsButAttributesDefer() throws Exception {
    // given
    Document document = createDocument();
    Element metaNode = (Element) document.getElementsByTagName("akn:meta").item(0);
    metaNode.appendChild(document.createElement("akn:identification"));
    metaNode.appendChild(document.createElement("akn:classification"));
    Element existingClassificationElement = document.createElement("akn:classification");
    existingClassificationElement.setAttribute("source", "source");
    metaNode.appendChild(existingClassificationElement);
    metaNode.appendChild(document.createElement("akn:references"));
    Element classificationElement = document.createElement("akn:classification");
    classificationElement.setAttribute("source", "source");
    classificationElement.setAttribute("ris:titel", "test");

    // when
    XmlAppender.insertOrderedElementIfNotExists(metaNode, classificationElement);

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).containsIgnoringWhitespaces(
      """
      <akn:meta>
        <akn:identification/>
        <akn:classification/>
        <akn:classification source="source"/>
        <akn:classification ris:titel="test" source="source"/>
        <akn:references/>
      </akn:meta>"""
    );
  }

  @Test
  @DisplayName(
    "Do not insert new classification element because there is an existing with same attribute list"
  )
  void insertOrderedElementIfNotExist_elementExists() throws Exception {
    // given
    Document document = createDocument();
    Element metaNode = (Element) document.getElementsByTagName("akn:meta").item(0);
    metaNode.appendChild(document.createElement("akn:identification"));
    metaNode.appendChild(document.createElement("akn:classification"));
    Element existingClassificationElement = document.createElement("akn:classification");
    existingClassificationElement.setAttribute("ris:titel", "test");
    metaNode.appendChild(existingClassificationElement);
    metaNode.appendChild(document.createElement("akn:references"));

    Element classificationElement = document.createElement("akn:classification");
    classificationElement.setAttribute("ris:titel", "test");

    // when
    XmlAppender.insertOrderedElementIfNotExists(metaNode, classificationElement);

    // then
    String xml = LiteratureXmlWriter.xmlToString(document);
    assertThat(xml).containsIgnoringWhitespaces(
      """
      <akn:meta>
        <akn:identification/>
        <akn:classification/>
        <akn:classification ris:titel="test"/>
        <akn:references/>
      </akn:meta>"""
    );
  }

  private static Stream<Arguments> attributeValues() {
    return Stream.of(
      Arguments.of("Test-\r\nlauf\n mit Zeilenumbrüchen", "Test- lauf mit Zeilenumbrüchen"),
      Arguments.of(
        "Testlauf   mit       sehr            vielen überflüssigen Leerzeichen",
        "Testlauf mit sehr vielen überflüssigen Leerzeichen"
      ),
      Arguments.of("      Testlauf         ", "Testlauf"),
      Arguments.of("No change needed", "No change needed"),
      Arguments.of("   Everything\r\n\n\n\n combined  together ", "Everything combined together")
    );
  }

  @ParameterizedTest
  @MethodSource("attributeValues")
  void normalizeAttributeValue(String attributeValue, String expected) {
    // given

    // when
    String actual = XmlAppender.normalizeAttributeValue(attributeValue);

    // then
    assertThat(actual).isEqualTo(expected);
  }

  private static Document createDocument()
    throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    return documentBuilder.parse(new InputSource(new StringReader(TEST_LDML)));
  }
}
