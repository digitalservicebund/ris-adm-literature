package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Jaxb frbrAlias element.
 */
@NoArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class FrbrAlias {

  @XmlAttribute
  private String name;

  @XmlAttribute
  private String value;
}
