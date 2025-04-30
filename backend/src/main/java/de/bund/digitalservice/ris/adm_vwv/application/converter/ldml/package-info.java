@XmlSchema(
  elementFormDefault = XmlNsForm.QUALIFIED,
  xmlns = {
    @XmlNs(prefix = "akn", namespaceURI = AkomaNtoso.AKN_NS),
    @XmlNs(prefix = "ris", namespaceURI = AkomaNtoso.RIS_NS),
  }
)
package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
