package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Jaxb doc element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Doc {

  @XmlAttribute
  private String name = "offene-struktur";

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private Meta meta;

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private Preface preface;

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private MainBody mainBody;
}
