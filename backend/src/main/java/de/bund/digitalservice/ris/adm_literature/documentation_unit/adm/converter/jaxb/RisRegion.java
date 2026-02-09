package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;

/**
 * Jaxb ris:region element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisRegion {

  @XmlAttribute
  private String domainTerm = "Region";

  @XmlValue
  private String value;
}
