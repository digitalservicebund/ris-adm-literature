package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Jaxb implicitReference element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ImplicitReference {

  @XmlAttribute
  private String shortForm;

  @XmlAttribute
  private String showAs;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisNormReference normReference;

  @XmlElement(namespace = XmlNamespace.RIS_NS)
  private RisCaselawReference caselawReference;

  /**
   * Returns the type of this reference.
   * @return The implicit reference type
   */
  public ImplicitReferenceType getReferenceType() {
    ImplicitReferenceType referenceType = ImplicitReferenceType.FUNDSTELLE;
    if (normReference != null) {
      referenceType = ImplicitReferenceType.ACTIVE_REFERENCE;
    } else if (caselawReference != null) {
      referenceType = ImplicitReferenceType.ACTIVE_CITATION;
    }
    return referenceType;
  }
}
