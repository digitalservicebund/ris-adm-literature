@XmlSchema(
  elementFormDefault = XmlNsForm.QUALIFIED,
  xmlns = {
    @XmlNs(prefix = "akn", namespaceURI = XmlNamespace.AKN_NS),
    @XmlNs(prefix = "ris", namespaceURI = XmlNamespace.RIS_NS),
  }
)
package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
