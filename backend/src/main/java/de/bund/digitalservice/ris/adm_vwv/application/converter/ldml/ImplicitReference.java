package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Jaxb implicitReference element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ImplicitReference {

  @XmlAttribute
  private String shortForm;

  @XmlAttribute
  private String showAs;

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private RisNormReference normReference;

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private RisCaselawReference caselawReference;
}
