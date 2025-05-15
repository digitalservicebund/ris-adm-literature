package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.XmlWriter;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.JaxbHtml;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.MainBody;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Kurzreferat transformer.
 */
@RequiredArgsConstructor
public class KurzreferatTransformer {

  private final AkomaNtoso akomaNtoso;
  private final XmlWriter xmlWriter = new XmlWriter();

  /**
   * Transforms the {@code AkomaNtoso} object to a 'Kurzreferat' HTML string.
   *
   * @return The 'Kurzreferat' HTML, or {@code null} if the surrounding {@code <mainBody>} element is {@code null}.
   */
  public String transform() {
    MainBody mainBody = akomaNtoso.getDoc().getMainBody();
    if (mainBody == null || mainBody.getHcontainer() != null) {
      // There are documents without a Kurzreferat. For pleasing LDML those documents do not contain a <div> tag,
      // but a <hcontainer> tag.
      return null;
    }
    // Filter out empty text nodes (with only whitespaces)
    List<Object> filteredHtml = mainBody
      .getDiv()
      .getHtml()
      .stream()
      .filter(htmlNode -> !(htmlNode instanceof String s && s.matches("\\s*")))
      .toList();
    JaxbHtml filteredDiv = new JaxbHtml();
    filteredDiv.setHtml(filteredHtml);
    return xmlWriter
      .writeXml(filteredDiv, false)
      // Replace wrapper element completely
      .replaceAll("</?jaxbHtml.*>", "")
      // Remove akn prefix from tag names, e.g. <akn:p> is transformed to <p>
      .replaceAll("<(/?)akn:([^<]*)>", "<$1$2>")
      .strip();
  }
}
