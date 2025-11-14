package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Jaxb frbrDate element.
 */
@NoArgsConstructor
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class FrbrDate {

  @XmlAttribute
  private String date;

  @XmlAttribute
  private String name = "entscheidungsdatum";
}
