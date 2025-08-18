package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.XmlWriter;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.JaxbHtml;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.MainBody;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Kurzreferat transformer.
 */
@Component
@RequiredArgsConstructor
public class KurzreferatTransformer {

  private final XmlWriter xmlWriter;

  /**
   * Transforms the {@code AkomaNtoso} object to a 'Kurzreferat' HTML string.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return The 'Kurzreferat' HTML, or {@code null} if the surrounding {@code <mainBody>} element is {@code null}.
   */
  public String transform(AkomaNtoso akomaNtoso) {
    MainBody mainBody = akomaNtoso.getDoc().getMainBody();
    if (mainBody == null || mainBody.getHcontainer() != null) {
      // There are documents without a Kurzreferat. For pleasing LDML those documents do not contain a <div> tag,
      // but a <hcontainer> tag.
      return null;
    }
    // Filter out empty text nodes (with only whitespaces)
    List<?> filteredHtml = mainBody
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
