package de.bund.digitalservice.ris.adm_literature.config;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.JaxbHtml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jaxb configuration to hold a singleton jaxb context bean.
 */
@Configuration
@Slf4j
public class JaxbConfiguration {

  /**
   * Creates a jaxb context bean.
   * @return Returns a jaxb context bean
   * @throws JAXBException In case the context could not be created
   */
  @Bean
  public JAXBContext jaxbContext() throws JAXBException {
    // Create JAXB instance with all possible root elements
    JAXBContext jaxbContext = JAXBContext.newInstance(AkomaNtoso.class, JaxbHtml.class);
    log.info("JAXB context created.");
    return jaxbContext;
  }
}
