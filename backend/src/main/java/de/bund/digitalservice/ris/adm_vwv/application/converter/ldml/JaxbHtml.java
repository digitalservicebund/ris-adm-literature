package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import java.util.Objects;
import lombok.*;

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

  public static JaxbHtml build(List<Object> html) {
    if (html == null || html.isEmpty() || html.stream().allMatch(Objects::isNull)) {
      return null;
    }

    return new JaxbHtml(html);
  }

  public JaxbHtml(List<Object> html) {
    this.html = html;
  }
}
