package de.bund.digitalservice.ris.adm_vwv.application.converter.util;

import jakarta.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@UtilityClass
class XmlAppender {

  static void addAttributeToNode(@Nonnull Node node, String attributeName, String attributeValue) {
    Attr attribute = node.getOwnerDocument().createAttribute(attributeName);
    attribute.setValue(normalizeAttributeValue(attributeValue));
    node.getAttributes().setNamedItem(attribute);
  }

  static String normalizeAttributeValue(@Nonnull String text) {
    return text
      // Replace line feeds with whitespace
      .replaceAll("\\R", " ")
      // Replace 2 whitespaces in a row with one whitespace
      .replaceAll("\\s{2,}", " ")
      // Remove leading and trailing whitespaces
      .strip();
  }

  /**
   * Inserts a new element into the given parent element, respecting the children
   * order if known.
   * <p>
   * <b>NOTE:</b>
   * </p>
   * If an element with the same tag name like the new element to insert already
   * exists,
   * the method appends the new element to insert right after the existing
   * element.
   *
   * @param parent     The parent element where to insert
   * @param newElement The element to insert
   */
  public static void insertOrderedElement(@Nonnull Element parent, @Nonnull Element newElement) {
    List<String> orderedChildrenNames = OrderedChildren.findChildrenNames(parent.getTagName());
    int newElementDesiredIndex = orderedChildrenNames.indexOf(newElement.getTagName());
    if (newElementDesiredIndex == -1) {
      // If new element tag name is not in desired order, then just append it to the
      // parent
      parent.appendChild(newElement);
      return;
    }
    Node referenceChild = null;
    NodeList parentChildNodes = parent.getChildNodes();
    for (int i = newElementDesiredIndex + 1; i < orderedChildrenNames.size(); i++) {
      String nextTagName = orderedChildrenNames.get(i);
      // Search for next child in order to insert new element before it
      referenceChild = IntStream.range(0, parentChildNodes.getLength())
        .mapToObj(parentChildNodes::item)
        // Only look for elements, ignore comments and texts
        .filter(childNode -> childNode.getNodeType() == Node.ELEMENT_NODE)
        .filter(childNode -> childNode.getNodeName().equals(nextTagName))
        .findFirst()
        .orElse(null);
      if (referenceChild != null) {
        parent.insertBefore(newElement, referenceChild);
        break;
      }
    }
    if (referenceChild == null) {
      // Fallback if no reference child could be found
      parent.appendChild(newElement);
    }
  }

  /**
   * Inserts a new element into the given parent element, respecting the children
   * order if known. If the given
   * new element already exists as child in the given parent, then it is not
   * inserted. The check of existence
   * return true if the tag name is equal and the attribute list (keys, values and
   * order) are equal.
   *
   * @param parent     The parent element where to insert
   * @param newElement The element to insert
   * @return The given new element if inserted, ot the existing element
   */
  public static Element insertOrderedElementIfNotExists(
    @Nonnull Element parent,
    @Nonnull Element newElement
  ) {
    List<Pair<String, String>> attributePairs = NodeToList.toAttributePairs(
      newElement.getAttributes()
    );
    Optional<Element> optionalElement = NodeToList.toList(parent.getChildNodes())
      .stream()
      .filter(Element.class::isInstance)
      .map(Element.class::cast)
      // Filter for same tag name
      .filter(childElement -> childElement.getTagName().equals(newElement.getTagName()))
      // Check if attributes of found child are equals of the given new element's
      // attributes
      .filter(childElement ->
        NodeToList.toAttributePairs(childElement.getAttributes()).equals(attributePairs)
      )
      .findFirst();
    return optionalElement.orElseGet(() -> {
      insertOrderedElement(parent, newElement);
      return newElement;
    });
  }

  @RequiredArgsConstructor
  enum OrderedChildren {
    FRBR_WORK(
      "akn:FRBRWork",
      List.of(
        "akn:FRBRthis",
        "akn:FRBRuri",
        "akn:FRBRalias",
        "akn:FRBRdate",
        "akn:FRBRauthor",
        "akn:componentInfo",
        "akn:preservation",
        "akn:FRBRcountry",
        "akn:FRBRsubtype",
        "akn:FRBRnumber",
        "akn:FRBRname",
        "akn:FRBRprescriptive",
        "akn:FRBRauthoritative"
      )
    ),
    FRBR_EXPRESSION(
      "akn:FRBRExpression",
      List.of(
        "akn:FRBRthis",
        "akn:FRBRuri",
        "akn:FRBRalias",
        "akn:FRBRdate",
        "akn:FRBRauthor",
        "akn:componentInfo",
        "akn:preservation",
        "akn:FRBRversionNumber",
        "akn:FRBRauthoritative",
        "akn:FRBRmasterExpression",
        "akn:FRBRlanguage",
        "akn:FRBRtranslation"
      )
    ),
    META(
      "akn:meta",
      List.of(
        "akn:identification",
        "akn:publication",
        "akn:classification",
        "akn:lifecycle",
        "akn:workflow",
        "akn:analysis",
        "akn:temporalData",
        "akn:references",
        "akn:notes",
        "akn:proprietary",
        "akn:presentation"
      )
    ),
    REFERENCES(
      "akn:references",
      List.of(
        "akn:original",
        "akn:passiveRef",
        "akn:activeRef",
        "akn:jurisprudence",
        "akn:hasAttachment",
        "akn:attachmentOf",
        "akn:TLCPerson",
        "akn:TLCOrganization",
        "akn:TLCConcept",
        "akn:TLCObject",
        "akn:TLCEvent",
        "akn:TLCLocation",
        "akn:TLCProcess",
        "akn:TLCRole",
        "akn:TLCTerm",
        "akn:TLCReference"
      )
    ),
    RIS_META(
      "ris:meta",
      List.of(
        "ris:sachgebiete",
        "ris:veroeffentlichungsJahre",
        "ris:gliederung",
        "ris:gesamttitelAngaben",
        "ris:regionen",
        "ris:zuordnungen",
        "ris:historicAdministrativeData",
        "ris:dubletteToleriert",
        "ris:verweisnotationen",
        "ris:problemkreise",
        "ris:teilbaende",
        "ris:ausgabe",
        "ris:besitznachweise",
        "ris:titelkurzformen",
        "ris:kollation",
        "ris:sonstigerSachtitelListe",
        "ris:vorauflagen",
        "ris:bibliothekarischeVerweisungen"
      )
    );

    private final String tagName;

    @Getter
    private final List<String> childrenNames;

    static List<String> findChildrenNames(String tagName) {
      return Arrays.stream(OrderedChildren.values())
        .filter(oc -> oc.tagName.equals(tagName))
        .map(OrderedChildren::getChildrenNames)
        .findFirst()
        .orElse(List.of());
    }
  }
}
