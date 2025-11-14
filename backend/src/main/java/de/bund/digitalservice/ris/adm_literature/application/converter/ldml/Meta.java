package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Jaxb meta element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Meta {

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private Identification identification;

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private Classification classification;

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private Analysis analysis;

  @XmlElement(namespace = XmlNamespace.AKN_NS)
  private Proprietary proprietary;

  /**
   * Returns the set proprietary instance or creates and sets a new one including an instance
   * of {@link RisMeta}.
   *
   * @return Instance of {@code Proprietary}
   */
  public Proprietary getOrCreateProprietary() {
    if (proprietary == null) {
      proprietary = new Proprietary();
      proprietary.setMeta(new RisMeta());
    }
    return proprietary;
  }

  /**
   * Returns the set analysis instance or creates and sets a new one including an instance
   * of {@link OtherReferences} with empty list of {@link ImplicitReference}.
   *
   * @return Instance of {@code Analysis}
   */
  public Analysis getOrCreateAnalysis() {
    if (analysis == null) {
      analysis = new Analysis();
      OtherReferences otherReferences = new OtherReferences();
      otherReferences.setImplicitReferences(new ArrayList<>());
      analysis.setOtherReferences(List.of(otherReferences));
    }
    return analysis;
  }
}
