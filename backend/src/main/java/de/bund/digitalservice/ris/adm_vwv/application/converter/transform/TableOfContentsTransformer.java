package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;

import java.util.List;
import java.util.Optional;

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
    Optional<Proprietary> proprietary = Optional.ofNullable(
      akomaNtoso.getDoc().getMeta().getProprietary()
    );

    if (proprietary.isEmpty()) return List.of();
    else return proprietary.get().getMetadata().getTableOfContentsEntries();
  }
}
