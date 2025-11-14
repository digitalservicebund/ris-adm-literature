package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Xml writer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XmlWriter {

  private final JAXBContext jaxbContext;

  /**
   * Transforms a JAXb element into a string. The output contains an XML header node.
   * @param jaxbElement The JAXb element to transform
   * @return String representation of the given JAXB element
   */
  public String writeXml(@Nonnull Object jaxbElement) {
    return writeXml(jaxbElement, true);
  }

  /**
   * Transforms a JAXb element into a string.
   * @param jaxbElement The JAXb element to transform
   * @param includeHeader If set to {@code true}, then the output contains an XML header node, otherwise set to
   *                      {@code false}
   * @return String representation of the given JAXB element
   */
  public String writeXml(@Nonnull Object jaxbElement, boolean includeHeader) {
    try {
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, !includeHeader);
      StringWriter stringWriter = new StringWriter();
      marshaller.marshal(jaxbElement, stringWriter);
      return stringWriter.toString();
    } catch (JAXBException e) {
      log.error("Could not write xml due to JAXBException.", e);
      throw new IllegalStateException(e);
    }
  }
}
