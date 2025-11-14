package de.bund.digitalservice.ris.adm_literature.application.converter.util;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * LDML element class for adding elements and attributes.
 */
@RequiredArgsConstructor
public class LdmlElement {

  @Getter
  private final Element element;

  /**
   * Creates a new instance with the owner document from the  given ldml element. This constructor is mainly useful,
   * if an element needs to be prepared with attributes in order to append it if it is not existing.
   *
   * @param ldmlElement The ldml element to take the owner document from
   * @param tagName The tag name
   */
  public LdmlElement(LdmlElement ldmlElement, String tagName) {
    element = ldmlElement.getElement().getOwnerDocument().createElement(tagName);
  }

  /**
   * Creates a new element with the given tag name, appends it to this element and returns it.
   * @param tagName The tag name of the element to create
   * @return The newly created element
   */
  public LdmlElement appendElementAndGet(String tagName) {
    Element newChild = element.getOwnerDocument().createElement(tagName);
    XmlAppender.insertOrderedElement(element, newChild);
    return new LdmlElement(newChild);
  }

  /**
   * Appends the given ldml element instance as child to this element, if it is not already existing.
   * @param newChild The ldml element to append
   * @return The given ldml element
   */
  public LdmlElement appendElementOnceAndGet(LdmlElement newChild) {
    return new LdmlElement(
      XmlAppender.insertOrderedElementIfNotExists(element, newChild.getElement())
    );
  }

  /**
   * Appends the given node to this ldml element.
   * @param node the node to append
   * @return This ldml element
   */
  public LdmlElement appendNode(Node node) {
    element.appendChild(node);
    return this;
  }

  /**
   * Appends the given text to this ldml element.
   * @param text The text to append
   * @return This ldml element
   */
  public LdmlElement appendText(String text) {
    Text newChildText = element.getOwnerDocument().createTextNode(text);
    element.appendChild(newChildText);
    return this;
  }

  /**
   * Adds the specified attribute to this ldml element, if {@code attributeValue} is not blank.
   * @param attributeName The attribute name
   * @param attributeValue The attribute value
   * @return This ldml element
   */
  public LdmlElement addAttribute(@Nonnull String attributeName, String attributeValue) {
    if (StringUtils.isNotBlank(attributeValue)) {
      XmlAppender.addAttributeToNode(element, attributeName, attributeValue);
    }
    return this;
  }

  /**
   * Returns a new ldml element template with this ldml element as parent.
   * @param tagName The tag name for the new ldml element
   * @return Ldml element template
   */
  public LdmlElementTemplate prepareElement(String tagName) {
    return new LdmlElementTemplate(tagName);
  }

  /**
   * Class for preparing an LDML element with attributes. This class can be used to prepare an element before
   * it is appended to a parent element, e.g. if an element with same attributes must be appended only once. Users
   * have to call {@link #appendOnce()} to append this instance to its parent.
   */
  @RequiredArgsConstructor
  @Getter
  public class LdmlElementTemplate {

    private final String tagName;
    private final Map<String, String> attributes = new HashMap<>();

    /**
     * Adds the specified attribute to this ldml element template, if {@code attributeValue} is not blank.
     * @param attributeName The attribute name
     * @param attributeValue The attribute value
     * @return This ldml element template
     */
    public LdmlElementTemplate addAttribute(@Nonnull String attributeName, String attributeValue) {
      if (StringUtils.isNotBlank(attributeValue)) {
        attributes.put(attributeName, attributeValue);
      }
      return this;
    }

    /**
     * Appends this element template as real element to the parent but only if it does not already exist.
     * @return Appended ldml element or an existing ldml element with same tag name and attributes
     */
    public LdmlElement appendOnce() {
      LdmlElement newChild = new LdmlElement(
        LdmlElement.this.getElement().getOwnerDocument().createElement(tagName)
      );
      getAttributes().forEach(newChild::addAttribute);
      return LdmlElement.this.appendElementOnceAndGet(newChild);
    }
  }
}
