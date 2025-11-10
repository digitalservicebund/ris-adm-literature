package de.bund.digitalservice.ris.adm_vwv.application.converter.util;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Slf4j
public class MinimalLdmlDocument {

  // TODO: Check which values should be in
  // bei <akn:FRBRdate date="$CURRENT_DATE" name="migration" ris:domainTerm="migration"/>
  // see https://digitalservicebund.atlassian.net/wiki/spaces/VER/pages/2089189440/LDML+Harmonization+LDML+as+Created+by+NeuRIS+Streams#Datum
  public static final String MINIMAL_LDML =
    """
    <?xml version="1.0" encoding="UTF-8"?>
    <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0" xmlns:ris="$RIS_NAMESPACE">
        <akn:doc name="offene-struktur">
            <akn:meta>
                <akn:identification source="attributsemantik-noch-undefiniert">
                    <akn:FRBRWork>
                        <akn:FRBRthis value="TODO"/>
                        <akn:FRBRuri value="TODO"/>
                        <akn:FRBRdate date="$CURRENT_DATE" name="TODO"/>
                        <akn:FRBRauthor href="TODO"/>
                        <akn:FRBRcountry value="de"/>
                        <akn:FRBRnumber value="TODO"/>
                        <akn:FRBRname value="TODO"/>
                    </akn:FRBRWork>
                    <akn:FRBRExpression>
                        <akn:FRBRthis value="TODO"/>
                        <akn:FRBRuri value="TODO"/>
                        <akn:FRBRdate date="$CURRENT_DATE" name="TODO"/>
                        <akn:FRBRauthor href="TODO"/>
                        <akn:FRBRlanguage language="deu"/>
                    </akn:FRBRExpression>
                    <akn:FRBRManifestation>
                        <akn:FRBRthis value="TODO"/>
                        <akn:FRBRuri value="TODO"/>
                        <akn:FRBRdate date="$CURRENT_DATE" name="TODO"/>
                        <akn:FRBRauthor href="TODO"/>
                        <akn:FRBRformat value="xml"/>
                    </akn:FRBRManifestation>
                </akn:identification>
            </akn:meta>
            <akn:preface>
            </akn:preface>
            <akn:mainBody>
            <akn:hcontainer name="crossheading"/>
            </akn:mainBody>
        </akn:doc>
    </akn:akomaNtoso>""".lines()
      .map(String::strip)
      .collect(Collectors.joining());

  private final DocumentBuilder documentBuilder;

  public MinimalLdmlDocument() {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      log.error("Could not create DocumentBuilderFactory", e);
      throw new IllegalStateException("Could not create DocumentBuilderFactory", e);
    }
  }

  public LdmlDocument create(LiteratureDocumentCategory documentType)
    throws IOException, SAXException {
    String currentDate = LocalDate.now().toString();
    String populatedLdml = MINIMAL_LDML.replace("$CURRENT_DATE", currentDate).replace(
      "$RIS_NAMESPACE",
      documentType.getNamespace()
    );
    return new LdmlDocument(
      documentBuilder.parse(new InputSource(new StringReader(populatedLdml)))
    );
  }
}
