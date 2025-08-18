package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Jaxb html element representing arbitrary xml content (for mapping html).
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class JaxbHtml {

  @XmlAttribute
  private String name;

  @XmlAnyElement
  @XmlMixed
  private List<?> html;
}
