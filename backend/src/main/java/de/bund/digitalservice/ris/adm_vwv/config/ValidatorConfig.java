package de.bund.digitalservice.ris.adm_vwv.config;

import de.bund.digitalservice.ris.adm_vwv.adapter.publishing.validation.SchemaBasedXmlValidator;
import de.bund.digitalservice.ris.adm_vwv.adapter.publishing.validation.XmlValidator;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {

  @Bean("bsgVwvValidator")
  public XmlValidator bsgVwvValidator() {
    return new SchemaBasedXmlValidator(
      List.of(
        "/schemas/akomaNtoso/akomantoso30.xsd",
        "/schemas/proprietary/bsg-vwv/ldml-ris-metadata.xsd"
      )
    );
  }
}
