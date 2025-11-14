package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Jaxb frbrSubtype element.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class FrbrSubtype {

  @XmlAttribute
  private String value;
}
