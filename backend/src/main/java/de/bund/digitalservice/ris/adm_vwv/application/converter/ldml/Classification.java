package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.*;

/**
 * Jaxb classification element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Classification {

  @XmlAttribute
  private String source = "attributsemantik-noch-undefiniert";

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private List<Keyword> keyword;
}
