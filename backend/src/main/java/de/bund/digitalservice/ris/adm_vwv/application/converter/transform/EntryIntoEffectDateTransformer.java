package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import lombok.RequiredArgsConstructor;

/**
 * Long title transformer.
 */
@RequiredArgsConstructor
public class EntryIntoEffectDateTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code EntryIntoEffectDate} object to a string.
   *
   * @return The entryIntoEffectDate
   */
  public String transform() {
    return akomaNtoso.getDoc().getMeta().getProprietary().getMetadata().getEntryIntoEffectDate();
  }
}
