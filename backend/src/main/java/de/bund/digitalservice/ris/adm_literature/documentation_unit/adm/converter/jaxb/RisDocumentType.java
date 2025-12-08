package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

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
