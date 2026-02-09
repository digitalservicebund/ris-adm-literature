package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Data;

/**
 * Jaxb ris:regionen element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisRegionen {

  @XmlAttribute
  private String domainTerm = "Regionen";

  @XmlElement(name = "region", namespace = XmlNamespace.RIS_NS)
  private List<RisDomainTerm> regionen;
}
