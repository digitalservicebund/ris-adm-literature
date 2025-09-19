package de.bund.digitalservice.ris.adm_vwv.adapter.publishing.validation;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

@Slf4j
public class SchemaBasedXmlValidator implements XmlValidator {

  private final Schema schema;

  /**
   * Creates a validator instance for a specific set of schemas.
   * @param schemaClasspathPaths A list of classpath paths to the XSD files.
   */
  public SchemaBasedXmlValidator(List<String> schemaClasspathPaths) {
    if (schemaClasspathPaths == null || schemaClasspathPaths.isEmpty()) {
      throw new IllegalArgumentException("At least one schema path must be provided.");
    }
    log.info("Initializing XML validator for schemas: {}", schemaClasspathPaths);
    try {
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file");
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

      StreamSource[] schemaSources = schemaClasspathPaths
        .stream()
        .map(this::streamSourceFromClasspath)
        .toArray(StreamSource[]::new);

      this.schema = factory.newSchema(schemaSources);
      log.info("Successfully initialized XML schemas.");
    } catch (SAXException e) {
      log.error("Failed to initialize XML schema.", e);
      throw new IllegalStateException("Could not compile the XSD schemas", e);
    }
  }

  @Override
  public void validate(String xmlContent) throws IOException, SAXException {
    Validator validator = schema.newValidator();
    validator.validate(new StreamSource(new StringReader(xmlContent)));
  }

  private StreamSource streamSourceFromClasspath(String path) {
    URL resourceUrl = this.getClass().getResource(path);
    if (resourceUrl == null) {
      throw new IllegalArgumentException("Schema resource not found in classpath: " + path);
    }
    return new StreamSource(resourceUrl.toExternalForm());
  }
}
