package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * Jaxb ris:normReference element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisNormReference {

  @XmlAttribute
  private String singleNorm;

  @XmlAttribute
  private String dateOfRelevance;

  @XmlAttribute
  private String dateOfVersion;
}
