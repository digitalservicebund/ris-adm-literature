package de.bund.digitalservice.ris.adm_vwv.application.converter.util;

import static javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD;

import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;

/**
 * Literature XML writer.
 */
@UtilityClass
public class LiteratureXmlWriter {

  public static String xmlToString(Document xmlDocument) throws TransformerException {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setAttribute(ACCESS_EXTERNAL_DTD, "");
    var transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    var result = new StreamResult(new StringWriter());
    var source = new DOMSource(xmlDocument);
    transformer.transform(source, result);
    return result.getWriter().toString();
  }
}
