package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * Jaxb ris:caselawReference element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisCaselawReference {

  @XmlAttribute
  private String abbreviation;

  @XmlAttribute
  private String court;

  @XmlAttribute
  private String courtLocation;

  @XmlAttribute
  private String date;

  @XmlAttribute
  private String documentNumber;

  @XmlAttribute
  private String referenceNumber;
}
