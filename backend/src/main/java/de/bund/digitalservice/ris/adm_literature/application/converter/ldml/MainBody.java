package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Jaxb mainBody element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class MainBody {

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private JaxbHtml div;

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private JaxbHtml hcontainer;
}
