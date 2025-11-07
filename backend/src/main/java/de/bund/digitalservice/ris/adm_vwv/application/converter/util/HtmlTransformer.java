package de.bund.digitalservice.ris.adm_vwv.application.converter.util;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@UtilityClass
class HtmlTransformer {

  private static final Set<String> ELEMENTS_TO_INLINE_ELEMENTS = Set.of(
    "em",
    "hlf",
    "noindex",
    "strong"
  );
  private static final Set<String> SUPPORTED_INLINE_ELEMENTS = Set.of(
    "a",
    "br",
    "em",
    "hlf",
    "noindex",
    "span",
    "strong",
    "sub",
    "sup"
  );

  /**
   * Transform a given source node to a new node of the given ldml document. Extract text content
   * and inline nodes, which are either getting a namespace "akn" or are replaced by an akn:inline element.
   *
   * @param ldmlDocument   The ldml document which will own the resulting node
   * @param sourceNode     The source node
   * @param newElementName Name of the element which is created in the ldml document
   * @return New node with transformed contents
   */
  public static Node transformNode(
    @Nonnull LdmlDocument ldmlDocument,
    @Nonnull Node sourceNode,
    @Nonnull String newElementName
  ) {
    Element newNode = createLdmlElement(ldmlDocument, newElementName);
    for (Node child : NodeToList.toList(sourceNode.getChildNodes())) {
      if (child.getNodeType() == Node.TEXT_NODE) {
        appendTextTo(ldmlDocument, newNode, child);
      } else if (child instanceof Element childElement) {
        String childTagName = childElement.getTagName().toLowerCase();
        Element newChildElement = newNode;
        if (SUPPORTED_INLINE_ELEMENTS.contains(childTagName)) {
          // Clone child with attributes
          Element newHtmlElement = createLdmlElement(ldmlDocument, childTagName);
          List<Pair<String, String>> attributes = NodeToList.toAttributePairs(
            childElement.getAttributes()
          );
          for (Pair<String, String> attribute : attributes) {
            newHtmlElement.setAttribute(attribute.getKey(), attribute.getValue());
          }
          newNode.appendChild(newHtmlElement);
          newChildElement = newHtmlElement;
        }
        // If childElement is not supported, e.g. for td, tr, table, blockquote, p we 'unwrap' the unsupported
        // child and transform it as new html element. This transforms '<td>Text</td>' into 'Text'.
        transform(ldmlDocument, childElement, newChildElement);
      }
    }
    return newNode;
  }

  private static Element createLdmlElement(LdmlDocument ldmlDocument, String newElementName) {
    return ELEMENTS_TO_INLINE_ELEMENTS.contains(newElementName)
      ? createInlineElement(ldmlDocument, newElementName)
      : ldmlDocument.createElement("akn:" + newElementName);
  }

  private static void transform(
    LdmlDocument ldmlDocument,
    Element childElement,
    Element newHtmlElement
  ) {
    List<Node> htmlChildren = NodeToList.toList(childElement.getChildNodes());
    for (Node htmlChild : htmlChildren) {
      if (htmlChild.getNodeType() == Node.TEXT_NODE) {
        appendTextTo(ldmlDocument, newHtmlElement, htmlChild);
      } else if (htmlChild.getNodeType() == Node.ELEMENT_NODE) {
        Node transformedHtmlChild = transformNode(ldmlDocument, htmlChild, htmlChild.getNodeName());
        newHtmlElement.appendChild(transformedHtmlChild);
      }
    }
  }

  private static void appendTextTo(LdmlDocument ldmlDocument, Element toElement, Node fromNode) {
    String strippedText = fromNode.getNodeValue().strip();
    if (strippedText.isEmpty()) {
      return;
    }
    if (
      toElement.getLastChild() != null && toElement.getLastChild().getNodeType() == Node.TEXT_NODE
    ) {
      strippedText = " " + strippedText;
    }
    toElement.appendChild(ldmlDocument.createTextNode(strippedText));
  }

  private static Element createInlineElement(LdmlDocument ldmlDocument, String name) {
    Element inline = ldmlDocument.createElement("akn:inline");
    XmlAppender.addAttributeToNode(inline, "name", name);
    return inline;
  }
}
