package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.*;

/**
 * Jaxb ris:metadata element.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RisMetadata {

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private List<RisNormgeber> normgeber;

  @XmlElementWrapper(name = "fieldsOfLaw", namespace = AkomaNtoso.RIS_NS)
  @XmlElement(name = "fieldOfLaw", namespace = AkomaNtoso.RIS_NS)
  private List<RisFieldOfLaw> fieldsOfLaw;

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private String entryIntoEffectDate;

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private String expiryDate;

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private RisDocumentType documentType;

  @XmlElementWrapper(name = "tableOfContentEntries", namespace = AkomaNtoso.RIS_NS)
  @XmlElement(name = "tableOfContentEntry", namespace = AkomaNtoso.RIS_NS)
  private List<String> tableOfContentEntries;

  @XmlElementWrapper(name = "zuordnungen", namespace = AkomaNtoso.RIS_NS)
  @XmlElement(name = "zuordnung", namespace = AkomaNtoso.RIS_NS)
  private List<RisZuordnung> zuordnungen;

  @XmlElementWrapper(name = "dateToQuoteList", namespace = AkomaNtoso.RIS_NS)
  @XmlElement(name = "dateToQuoteEntry", namespace = AkomaNtoso.RIS_NS)
  private List<String> dateToQuoteList;

  @XmlElementWrapper(name = "referenceNumbers", namespace = AkomaNtoso.RIS_NS)
  @XmlElement(name = "referenceNumber", namespace = AkomaNtoso.RIS_NS)
  private List<String> referenceNumbers;

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private JaxbHtml historicAdministrativeData;

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private String region;

  @XmlElement(namespace = AkomaNtoso.RIS_NS)
  private String historicAbbreviation;

  @XmlElementWrapper(name = "activeReferences", namespace = AkomaNtoso.RIS_NS)
  @XmlElement(name = "activeReference", namespace = AkomaNtoso.RIS_NS)
  private List<RisActiveReference> activeReferences;
}
