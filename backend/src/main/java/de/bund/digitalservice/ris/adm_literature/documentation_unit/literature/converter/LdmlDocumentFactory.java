package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.converter;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Factory class for creating instances of {@link LdmlDocument}.
 */
@Slf4j
@Component
public class LdmlDocumentFactory {

  private final DocumentBuilderFactory documentBuilderFactory;

  /**
   * Creates a new instance of this class.
   */
  private LdmlDocumentFactory() {
    try {
      documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    } catch (ParserConfigurationException e) {
      log.error("Could not create DocumentBuilderFactory", e);
      throw new IllegalStateException("Could not create DocumentBuilderFactory", e);
    }
  }

  /**
   * Creates a new minimal ldml document for the given literature document category.
   *
   * @param literatureDocumentCategory The literature document category
   * @return Ldml document instance
   * @throws IOException If an I/O error occurs
   * @throws SAXException If a parse error occurs
   */
  public LdmlDocument createDocument(
    @NonNull LiteratureDocumentCategory literatureDocumentCategory
  ) throws IOException, SAXException, ParserConfigurationException {
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    return new MinimalLdmlDocument().create(documentBuilder, literatureDocumentCategory);
  }

  /**
   * Creates a new instance based on the given xml.
   *
   * @param xml The xml string
   * @return Ldml document instance
   * @throws IOException If an I/O error occurs
   * @throws SAXException If a parse error occurs
   */
  public LdmlDocument createDocument(@NonNull String xml)
    throws IOException, SAXException, ParserConfigurationException {
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    return new LdmlDocument(documentBuilder.parse(new InputSource(new StringReader(xml))));
  }
}
