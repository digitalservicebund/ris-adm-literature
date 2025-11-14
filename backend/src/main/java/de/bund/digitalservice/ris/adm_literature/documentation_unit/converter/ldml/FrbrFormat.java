package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * Jaxb frbrFormat element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class FrbrFormat {

  @XmlAttribute
  private String value = "xml";
}
