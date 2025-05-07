package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.XmlWriter;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.JaxbHtml;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.MainBody;
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
    if (mainBody == null) {
      return null;
    }
    JaxbHtml div = mainBody.getDiv();
    return xmlWriter
      .writeXml(div, false)
      // Replace wrapper element completely
      .replaceAll("</?jaxbHtml.*>", "")
      // Remove akn prefix from tag names, e.g. <akn:p> is transformed to <p>
      .replaceAll("<(/?)akn:([^<]*)>", "<$1$2>");
  }
}
