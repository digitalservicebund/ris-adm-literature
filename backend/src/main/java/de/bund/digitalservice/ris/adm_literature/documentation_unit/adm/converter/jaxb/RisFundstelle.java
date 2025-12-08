package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * Jaxb ris:fundstelle element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisFundstelle {

  @XmlAttribute
  private String abbreviation;

  @XmlAttribute
  private String publicId;

  @XmlAttribute
  private String zitatstelle;
}
