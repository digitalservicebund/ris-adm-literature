package de.bund.digitalservice.ris.adm_vwv.adapter.publishing.validation;

import java.io.IOException;
import org.xml.sax.SAXException;

public interface XmlValidator {
  void validate(String xmlContent) throws IOException, SAXException;
}
