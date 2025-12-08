package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Jaxb longTitle element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class LongTitle {

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private JaxbHtml block;
}
