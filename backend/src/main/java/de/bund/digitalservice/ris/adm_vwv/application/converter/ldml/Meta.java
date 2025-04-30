package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Jaxb meta element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Meta {

  @XmlElement(namespace = AkomaNtoso.AKN_NS)
  private Identification identification;

  @XmlElement(namespace = AkomaNtoso.AKN_NS)
  private Classification classification;

  @XmlElement(namespace = AkomaNtoso.AKN_NS)
  private Analysis analysis;

  @XmlElement(namespace = AkomaNtoso.AKN_NS)
  private Proprietary proprietary;
}
