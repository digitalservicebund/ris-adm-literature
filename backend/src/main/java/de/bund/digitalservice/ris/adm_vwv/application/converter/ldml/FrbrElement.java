package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

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

  @XmlElement(name = "FRBRthis", namespace = AkomaNtoso.AKN_NS)
  private FrbrThis frbrThis;

  @XmlElement(name = "FRBRuri", namespace = AkomaNtoso.AKN_NS)
  private FrbrUri frbrUri;

  @XmlElement(name = "FRBRalias", namespace = AkomaNtoso.AKN_NS)
  private List<FrbrAlias> frbrAlias;

  @XmlElement(name = "FRBRdate", namespace = AkomaNtoso.AKN_NS)
  private FrbrDate frbrDate;

  @XmlElement(name = "FRBRauthor", namespace = AkomaNtoso.AKN_NS)
  private FrbrAuthor frbrAuthor;

  @XmlElement(name = "FRBRcountry", namespace = AkomaNtoso.AKN_NS)
  private FrbrCountry frbrCountry;

  @XmlElement(name = "FRBRlanguage", namespace = AkomaNtoso.AKN_NS)
  private FrbrLanguage frbrLanguage;

  @XmlElement(name = "FRBRformat", namespace = AkomaNtoso.AKN_NS)
  private FrbrFormat frbrFormat;

  @XmlElement(name = "FRBRsubtype", namespace = AkomaNtoso.AKN_NS)
  private FrbrSubtype frbrSubtype;

  @XmlElement(name = "FRBRnumber", namespace = AkomaNtoso.AKN_NS)
  private FrbrNumber frbrNumber;

  @XmlElement(name = "FRBRname", namespace = AkomaNtoso.AKN_NS)
  private FrbrName frbrName;
}
