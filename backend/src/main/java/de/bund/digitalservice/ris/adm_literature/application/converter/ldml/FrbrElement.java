package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Data;

/**
 * Jaxb frbr element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class FrbrElement {

  @XmlElement(name = "FRBRthis", namespace = XmlNamespace.AKN_NS)
  private FrbrThis frbrThis;

  @XmlElement(name = "FRBRuri", namespace = XmlNamespace.AKN_NS)
  private FrbrUri frbrUri;

  @XmlElement(name = "FRBRalias", namespace = XmlNamespace.AKN_NS)
  private List<FrbrAlias> frbrAlias;

  @XmlElement(name = "FRBRdate", namespace = XmlNamespace.AKN_NS)
  private FrbrDate frbrDate;

  @XmlElement(name = "FRBRauthor", namespace = XmlNamespace.AKN_NS)
  private FrbrAuthor frbrAuthor;

  @XmlElement(name = "FRBRcountry", namespace = XmlNamespace.AKN_NS)
  private FrbrCountry frbrCountry;

  @XmlElement(name = "FRBRlanguage", namespace = XmlNamespace.AKN_NS)
  private FrbrLanguage frbrLanguage;

  @XmlElement(name = "FRBRformat", namespace = XmlNamespace.AKN_NS)
  private FrbrFormat frbrFormat;

  @XmlElement(name = "FRBRsubtype", namespace = XmlNamespace.AKN_NS)
  private FrbrSubtype frbrSubtype;

  @XmlElement(name = "FRBRnumber", namespace = XmlNamespace.AKN_NS)
  private FrbrNumber frbrNumber;

  @XmlElement(name = "FRBRname", namespace = XmlNamespace.AKN_NS)
  private FrbrName frbrName;
}
