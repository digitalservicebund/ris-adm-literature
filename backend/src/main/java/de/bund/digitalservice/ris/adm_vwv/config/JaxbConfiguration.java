package de.bund.digitalservice.ris.adm_vwv.config;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.JaxbHtml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jaxb configuration to hold a singleton jaxb context bean.
 */
@Configuration
public class JaxbConfiguration {

  /**
   * Creates a jaxb context bean.
   * @return Returns a jaxb context bean
   * @throws JAXBException In case the context could not be created
   */
  @Bean
  public JAXBContext jaxbContext() throws JAXBException {
    // Create JAXB instance with all possible root elements
    return JAXBContext.newInstance(AkomaNtoso.class, JaxbHtml.class);
  }
}
