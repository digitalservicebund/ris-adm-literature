package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.*;

/**
 * Jaxb identification element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Identification {

  @XmlAttribute
  private String source = "attributsemantik-noch-undefiniert";

  @XmlElement(name = "FRBRWork", namespace = AkomaNtoso.AKN_NS)
  private FrbrElement frbrWork;

  @XmlElement(name = "FRBRExpression", namespace = AkomaNtoso.AKN_NS)
  private FrbrElement frbrExpression;

  @XmlElement(name = "FRBRManifestation", namespace = AkomaNtoso.AKN_NS)
  private FrbrElement frbrManifestation;
}
