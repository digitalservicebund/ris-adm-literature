package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Jaxb preface element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Preface {

  @XmlElement(namespace = AkomaNtoso.AKN_NS)
  private LongTitle longTitle;
}
