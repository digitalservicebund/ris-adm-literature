package de.bund.digitalservice.ris.adm_vwv.application.converter;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

/**
 * Xml writer.
 */
@Slf4j
public class XmlWriter {

  /**
   * Transforms a JAXb element into a string.
   * @param jaxbElement The JAXb element to transform
   * @return String representation of the given JAXB element
   */
  public String writeXml(@Nonnull Object jaxbElement) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(AkomaNtoso.class);
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      StringWriter stringWriter = new StringWriter();
      marshaller.marshal(jaxbElement, stringWriter);
      return stringWriter.toString();
    } catch (JAXBException e) {
      log.error("Could not write xml due to JAXBException.", e);
      throw new IllegalStateException(e);
    }
  }
}
