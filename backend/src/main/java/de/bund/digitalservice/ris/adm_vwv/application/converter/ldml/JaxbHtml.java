package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Jaxb html element representing arbitrary xml content (for mapping html).
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbHtml {

  @XmlAttribute
  private String name;

  @XmlAnyElement
  @XmlMixed
  private List<Object> html;

}
