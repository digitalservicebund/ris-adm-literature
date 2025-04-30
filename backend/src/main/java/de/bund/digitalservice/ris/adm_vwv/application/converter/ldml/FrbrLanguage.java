package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * Jaxb frbrLanguage element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class FrbrLanguage {

  @XmlAttribute
  private String language = "deu";
}
