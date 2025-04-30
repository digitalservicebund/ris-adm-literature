package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/**
 * Jaxb akomaNtoso root element.
 */
@Data
@XmlRootElement(name = "akomaNtoso", namespace = AkomaNtoso.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class AkomaNtoso {

  public static final String AKN_NS = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0";
  public static final String RIS_NS = "http://ldml.neuris.de/metadata/";

  @XmlElement(namespace = AkomaNtoso.AKN_NS)
  private Doc doc;
}
