package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/**
 * Jaxb akomaNtoso root element.
 */
@Data
@XmlRootElement(name = "akomaNtoso", namespace = XmlNamespace.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class AkomaNtoso {

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private Doc doc;
}
