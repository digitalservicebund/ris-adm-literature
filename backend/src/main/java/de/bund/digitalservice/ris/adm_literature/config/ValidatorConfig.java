package de.bund.digitalservice.ris.adm_literature.config;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.XmlValidator;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the XML Validators.
 */
@Configuration
public class ValidatorConfig {

  private static final String AKOMA_NTOSO_SCHEMA_LOCATION = "/schemas/akomaNtoso/akomantoso30.xsd";

  /**
   * Bean for bsg vwv validation
   * @return Configured bsg validator
   */
  @Bean("bsgVwvValidator")
  public XmlValidator bsgVwvValidator() {
    return new XmlValidator(
      List.of(AKOMA_NTOSO_SCHEMA_LOCATION, "/schemas/proprietary/bsg-vwv/ldml-ris-meta.xsd")
    );
  }

  /**
   * Bean for uli lit validation
   * @return Configured uli validator
   */
  @Bean("uliLiteratureValidator")
  public XmlValidator uliLiteratureValidator() {
    return new XmlValidator(
      List.of(
        AKOMA_NTOSO_SCHEMA_LOCATION,
        "/schemas/proprietary/lit/ldml-ris-literature.xsd",
        "/schemas/proprietary/uli/ldml-ris-literature-unselbstaendig-meta.xsd"
      )
    );
  }

  /**
   * Bean for sli lit validation
   * @return Configured sli validator
   */
  @Bean("sliLiteratureValidator")
  public XmlValidator sliLiteratureValidator() {
    return new XmlValidator(
      List.of(
        AKOMA_NTOSO_SCHEMA_LOCATION,
        "/schemas/proprietary/lit/ldml-ris-literature.xsd",
        "/schemas/proprietary/sli/ldml-ris-literature-selbstaendig-meta.xsd"
      )
    );
  }
}
