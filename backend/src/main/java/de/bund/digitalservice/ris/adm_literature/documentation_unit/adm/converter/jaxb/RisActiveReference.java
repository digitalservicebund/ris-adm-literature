package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * Jaxb ris:activeReference element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisActiveReference {

  @XmlAttribute
  private String typeNumber;

  @XmlAttribute
  private String reference;

  @XmlAttribute
  private String section;

  @XmlAttribute
  private String paragraph;

  @XmlAttribute
  private String subParagraph;

  @XmlAttribute
  private String position;

  @XmlAttribute
  private String dateOfVersion;
}
