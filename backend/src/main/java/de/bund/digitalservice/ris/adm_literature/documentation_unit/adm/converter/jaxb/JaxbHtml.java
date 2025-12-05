package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

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
