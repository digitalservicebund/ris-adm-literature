package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/**
 * Jaxb ris:documentType element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisDocumentType {

  @XmlAttribute
  private String category;

  @XmlAttribute
  private String longTitle;

  @XmlValue
  private String value;
}
