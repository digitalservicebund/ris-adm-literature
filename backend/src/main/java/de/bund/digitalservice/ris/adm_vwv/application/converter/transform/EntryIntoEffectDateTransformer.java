package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisMetadata;

import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * EntryIntoEffectDate transformer.
 */
@RequiredArgsConstructor
public class EntryIntoEffectDateTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code EntryIntoEffectDate} object to a string.
   *
   * @return The entryIntoEffectDate or null if Meta or Proprietary or Metadata does not exist
   */
  public String transform() {
     return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
                .map(Proprietary::getMetadata)
                .map(RisMetadata::getEntryIntoEffectDate)
                .orElse(null);
  }
}
