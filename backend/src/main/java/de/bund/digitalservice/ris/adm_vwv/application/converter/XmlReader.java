package de.bund.digitalservice.ris.adm_vwv.application.converter;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import jakarta.annotation.Nonnull;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import lombok.extern.slf4j.Slf4j;

/**
 * Xml reader.
 */
@Slf4j
public class XmlReader {

  /**
   * Transforms a plain string of XML into Java.
   * @param xml The XML string
   * @return An instance of {@link AkomaNtoso}, representing the read XML
   */
  public AkomaNtoso readXml(@Nonnull String xml) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(AkomaNtoso.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      return (AkomaNtoso) unmarshaller.unmarshal(new StringReader(xml));
    } catch (JAXBException e) {
      log.error("Could not read xml due to JAXBException.", e);
      throw new IllegalStateException(e);
    }
  }
}
