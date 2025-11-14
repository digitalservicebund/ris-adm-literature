package de.bund.digitalservice.ris.adm_literature.application.converter.transform;

import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.JaxbHtml;
import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.Preface;
import lombok.RequiredArgsConstructor;

/**
 * Long title transformer.
 */
@RequiredArgsConstructor
public class LongTitleTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a long title string.
   *
   * @return The long title, or {@code null} if the surrounding {@code <preface>} element is {@code null}
   */
  public String transform() {
    Preface preface = akomaNtoso.getDoc().getPreface();
    if (preface == null) {
      return null;
    }
    JaxbHtml block = preface.getLongTitle().getBlock();
    // This transformer preassumes that the block element contains only one text node
    return block.getHtml().getFirst().toString();
  }
}
