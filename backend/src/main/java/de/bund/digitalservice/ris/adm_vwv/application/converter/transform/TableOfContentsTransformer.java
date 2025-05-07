package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Transformer for 'Gliederung'.
 */
@RequiredArgsConstructor
public class TableOfContentsTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code TableOfContents} object to a list of references.
   *
   * @return List of table of contents entries or empty list
   */
  public List<String> transform() {
    return akomaNtoso.getDoc().getMeta().getProprietary().getMetadata().getTableOfContentsEntries();
  }
}
