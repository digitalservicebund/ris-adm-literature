package de.bund.digitalservice.ris.adm_literature.application.converter.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * LDML document class provides methods for writing LDML elements.
 */
@RequiredArgsConstructor
public class LdmlDocument {

  private static final String UNDEFINED = "attributsemantik-noch-undefiniert";

  @Getter
  private final Document document;

  public Element getMeta() {
    return getElement("akn:meta");
  }

  public LdmlElement risMeta() {
    return new LdmlElement(getElement("ris:meta"));
  }

  public LdmlElement mainBody() {
    return new LdmlElement(getElement("akn:mainBody"));
  }

  public LdmlElement preface() {
    return new LdmlElement(getElement("akn:preface"));
  }

  public LdmlElement frbrWork() {
    return new LdmlElement(getElement("akn:FRBRWork"));
  }

  public LdmlElement frbrExpression() {
    return new LdmlElement(getElement("akn:FRBRExpression"));
  }

  private Element getElement(String tagName) {
    return (Element) document.getElementsByTagName(tagName).item(0);
  }

  public Element createElement(String tagName) {
    return document.createElement(tagName);
  }

  public Text createTextNode(String text) {
    return document.createTextNode(text);
  }

  public LdmlElement addAnalysis() {
    return addChildTo(getMeta(), "akn:analysis", UNDEFINED);
  }

  public LdmlElement addClassification(String source) {
    return addChildTo(getMeta(), "akn:classification", source);
  }

  public LdmlElement addProprietary() {
    return addChildTo(getMeta(), "akn:proprietary", UNDEFINED);
  }

  public LdmlElement addReferences() {
    return addReferences(UNDEFINED);
  }

  public LdmlElement addReferences(String source) {
    return addChildTo(getMeta(), "akn:references", source);
  }

  private LdmlElement addChildTo(Element parent, String childName, String source) {
    Element child = document.createElement(childName);
    XmlAppender.addAttributeToNode(child, "source", source);
    return new LdmlElement(XmlAppender.insertOrderedElementIfNotExists(parent, child));
  }
}
