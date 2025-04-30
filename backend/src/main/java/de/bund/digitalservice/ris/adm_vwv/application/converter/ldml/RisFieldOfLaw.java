package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;

/**
 * Jaxb ris:fieldOfLaw element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisFieldOfLaw {

  @XmlAttribute
  private String notation;

  @XmlValue
  private String value;
}
