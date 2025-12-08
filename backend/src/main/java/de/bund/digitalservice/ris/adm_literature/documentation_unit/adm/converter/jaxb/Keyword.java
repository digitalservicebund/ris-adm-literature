package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Jaxb keyword element.
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Keyword {

  @XmlAttribute
  private String dictionary = "attributsemantik-noch-undefiniert";

  @XmlAttribute
  private String showAs;

  @XmlAttribute
  private String value;
}
