package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Xml reader class for reading a string into an {@link Node} instance.
 */
public class DomXmlReader {

  private final DocumentBuilder documentBuilder;

  DomXmlReader() {
    try {
      documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Could not create document builder.", e);
    }
  }

  /**
   * Reads the given xml string and returns a node representation of it.
   * @param xml The xml string
   * @return Node represents the given xml string
   */
  public Node readXml(String xml) {
    try {
      Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
      return document.getDocumentElement();
    } catch (SAXException | IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
