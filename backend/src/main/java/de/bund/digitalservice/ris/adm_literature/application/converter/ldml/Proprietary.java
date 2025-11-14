package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.*;

/**
 * Jaxb proprietary element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Proprietary {

  @XmlAttribute
  private String source = "attributsemantik-noch-undefiniert";

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisMeta meta;
}
