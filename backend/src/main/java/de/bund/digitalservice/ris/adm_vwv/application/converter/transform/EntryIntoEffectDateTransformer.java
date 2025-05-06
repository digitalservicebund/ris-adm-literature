package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
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
    var dateOptional = Optional.ofNullable(akomaNtoso.getDoc())
      .map(d -> d.getMeta())
      .map(m -> m.getProprietary())
      .map(p -> p.getMetadata())
     return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
                .map(Proprietary::getMetadata)
                .map(RisMetadata::getEntryIntoEffectDate)
                .orElse(null);

    if (dateOptional.isPresent()) return dateOptional.get();
    else return null;
  }
}
