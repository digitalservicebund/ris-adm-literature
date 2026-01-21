package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/**
 * Jaxb ris:referenz element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisReferenz {

  @XmlAttribute
  private String domainTerm = "Referenz";

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm richtung;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm referenzArt;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm dokumentnummer;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm uri;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm notiz;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm relativerPfad;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm dokumenttypAbkuerzung;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm dokumenttyp;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm titel;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm autor;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisDomainTerm veroeffentlichungsjahr;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private String standardzusammenfassung;
}
