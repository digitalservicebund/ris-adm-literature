package de.bund.digitalservice.ris.adm_vwv.application.converter;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.PublishingFailedException;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.IDocumentationContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.UliDocumentationUnitContent;
import jakarta.annotation.Nonnull;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

@Component
@Slf4j
public class UliLdmlConverterStrategy implements LdmlConverterStrategy {

  private final DocumentBuilder documentBuilder;
  private final Transformer transformer;

  // Namespaces
  private static final String AKN_NS = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0";
  private static final String RIS_NS = "http://ldml.neuris.de/ris/meta/";
  private static final String ULI_NS = "http://ldml.neuris.de/literature/unselbstaendig/meta/";

  public UliLdmlConverterStrategy() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      this.documentBuilder = factory.newDocumentBuilder();

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      this.transformer = transformerFactory.newTransformer();
      this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      this.transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    } catch (ParserConfigurationException | TransformerException e) {
      // This could be converted into a yet to be implemented TransformationFailedExpection
      throw new PublishingFailedException("Failed to initialize ULI converter strategy", e);
    }
  }

  @Override
  public boolean supports(IDocumentationContent content) {
    return content instanceof UliDocumentationUnitContent;
  }

  @Override
  public String convertToLdml(@Nonnull IDocumentationContent content, String previousXmlVersion) { // FIX: Rename parameter type
    UliDocumentationUnitContent uliContent = (UliDocumentationUnitContent) content;
    Document doc;

    if (previousXmlVersion != null) {
      try {
        // Parse the previous XML
        doc = documentBuilder.parse(new InputSource(new StringReader(previousXmlVersion)));
        // Clear existing meta, preface, and mainBody for repopulation
        clearChildElements(doc.getElementsByTagNameNS(AKN_NS, "meta").item(0));
        clearChildElements(doc.getElementsByTagNameNS(AKN_NS, "preface").item(0));
        clearChildElements(doc.getElementsByTagNameNS(AKN_NS, "mainBody").item(0));
      } catch (Exception e) {
        log.warn("Failed to parse previous XML, creating new document.", e);
        doc = createMinimalLdmlDocument();
      }
    } else {
      doc = createMinimalLdmlDocument();
    }

    // === Populate Akoma Ntoso structure based on XmlItemProcessor logic ===

    Element docElement = (Element) doc.getElementsByTagNameNS(AKN_NS, "doc").item(0);
    docElement.setAttribute("name", uliContent.documentNumber());
    Element meta = (Element) doc.getElementsByTagNameNS(AKN_NS, "meta").item(0);
    Element preface = (Element) doc.getElementsByTagNameNS(AKN_NS, "preface").item(0);
    mapDocumentNumber(doc, meta, uliContent.documentNumber());
    mapHauptsachtitel(doc, preface, uliContent.hauptsachtitel());
    mapDokumentTypen(doc, meta, uliContent.dokumentTyp());
    mapFrbrAlias(doc, meta, "haupttitelZusatz", uliContent.hauptsachtitelZusatz());
    mapFrbrAlias(doc, meta, "dokumentarischerTitel", uliContent.dokumentarischerTitel());
    mapNote(doc, meta, uliContent.note());
    Element uliMetaElement = createUliMetaDomElement(doc, uliContent);
    Element proprietary = getOrCreateChild(doc, meta, AKN_NS, "proprietary");
    proprietary.appendChild(uliMetaElement);

    return serializeDocument(doc);
  }

  private Document createMinimalLdmlDocument() {
    Document doc = documentBuilder.newDocument();
    Element akomaNtoso = doc.createElementNS(AKN_NS, "akn:akomaNtoso");
    doc.appendChild(akomaNtoso);

    Element docElement = doc.createElementNS(AKN_NS, "akn:doc");
    docElement.setAttribute("name", "placeholder"); // Will be overwritten
    akomaNtoso.appendChild(docElement);

    docElement.appendChild(doc.createElementNS(AKN_NS, "akn:meta"));
    docElement.appendChild(doc.createElementNS(AKN_NS, "akn:preface"));
    docElement.appendChild(doc.createElementNS(AKN_NS, "akn:mainBody"));

    return doc;
  }

  /**
   * Manually constructs the ULI proprietary meta block using W3C DOM.
   */
  private Element createUliMetaDomElement(Document doc, UliDocumentationUnitContent uliContent) {
    Element uliMeta = doc.createElementNS(ULI_NS, "meta");
    uliMeta.setPrefix("ris-uli");
    uliMeta.setAttribute("xmlns:ris-uli", ULI_NS);

    if (StringUtils.isNotBlank(uliContent.veroeffentlichungsjahr())) {
      Element jahre = doc.createElementNS(ULI_NS, "veroeffentlichungsJahre");
      Element jahr = doc.createElementNS(ULI_NS, "veroeffentlichungsJahr");
      jahr.setTextContent(uliContent.veroeffentlichungsjahr());
      jahre.appendChild(jahr);
      uliMeta.appendChild(jahre);
    }

    return uliMeta;
  }

  private void mapDocumentNumber(Document doc, Element meta, String documentNumber) {
    Element identification = getOrCreateChild(doc, meta, AKN_NS, "identification");
    Element frbrWork = getOrCreateChild(doc, identification, AKN_NS, "FRBRWork");

    Element frbrAlias = doc.createElementNS(AKN_NS, "akn:FRBRalias");
    frbrAlias.setAttribute("name", "Dokumentnummer");
    frbrAlias.setAttribute("value", documentNumber);
    frbrWork.appendChild(frbrAlias);

    Element frbrCountry = doc.createElementNS(AKN_NS, "akn:FRBRcountry");
    frbrCountry.setAttribute("value", "de");
    frbrWork.appendChild(frbrCountry);
  }

  private void mapHauptsachtitel(Document doc, Element preface, String hauptsachtitel) {
    if (StringUtils.isNotBlank(hauptsachtitel)) {
      Element longTitle = doc.createElementNS(AKN_NS, "akn:longTitle");
      Element block = doc.createElementNS(AKN_NS, "akn:block");
      block.setAttribute("name", "longTitle");
      block.setTextContent(hauptsachtitel); // unescapeHtml is not needed for setTextContent
      longTitle.appendChild(block);
      preface.appendChild(longTitle);
    }
  }

  private void mapDokumentTypen(Document doc, Element meta, List<DocumentType> dokumentTypen) {
    if (dokumentTypen == null || dokumentTypen.isEmpty()) {
      return;
    }
    Element identification = getOrCreateChild(doc, meta, AKN_NS, "identification");
    Element frbrWork = getOrCreateChild(doc, identification, AKN_NS, "FRBRWork");

    for (DocumentType typ : dokumentTypen) {
      Element subtype = doc.createElementNS(AKN_NS, "akn:FRBRsubtype");
      subtype.setAttribute("value", typ.abbreviation());
      frbrWork.appendChild(subtype);
    }
  }

  private void mapFrbrAlias(Document doc, Element meta, String name, String value) {
    if (StringUtils.isNotBlank(value)) {
      Element identification = getOrCreateChild(doc, meta, AKN_NS, "identification");
      Element frbrWork = getOrCreateChild(doc, identification, AKN_NS, "FRBRWork");

      Element frbrAlias = doc.createElementNS(AKN_NS, "akn:FRBRalias");
      frbrAlias.setAttribute("name", name);
      frbrAlias.setAttribute("value", value);
      frbrWork.appendChild(frbrAlias);
    }
  }

  private void mapNote(Document doc, Element meta, String noteText) {
    if (StringUtils.isNotBlank(noteText)) {
      Element notes = doc.createElementNS(AKN_NS, "akn:notes");
      notes.setAttribute("source", "gesamtfussnoten");

      Element note = doc.createElementNS(AKN_NS, "akn:note");
      Element block = doc.createElementNS(AKN_NS, "akn:block");
      block.setAttribute("name", "gesamtfussnote");
      block.setTextContent(noteText.strip());

      note.appendChild(block);
      notes.appendChild(note);
      meta.appendChild(notes);
    }
  }

  private Element getOrCreateChild(
    Document doc,
    Element parent,
    String namespaceURI,
    String tagName
  ) {
    Node existing = parent.getElementsByTagNameNS(namespaceURI, tagName).item(0);
    if (existing != null) {
      return (Element) existing;
    }
    Element child = doc.createElementNS(namespaceURI, "akn:" + tagName);
    parent.appendChild(child);
    return child;
  }

  private void clearChildElements(Node node) {
    if (node == null) return;
    while (node.hasChildNodes()) {
      node.removeChild(node.getFirstChild());
    }
  }

  private String serializeDocument(Document doc) {
    try {
      StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(writer));
      return writer.toString();
    } catch (TransformerException e) {
      log.error("Failed to serialize W3C Document", e);
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>" + e.getMessage() + "</error>";
    }
  }
}
