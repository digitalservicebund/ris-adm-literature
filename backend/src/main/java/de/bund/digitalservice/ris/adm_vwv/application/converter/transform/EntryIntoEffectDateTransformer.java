package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import java.util.Optional;

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
   * @return The entryIntoEffectDate or null if Meta or Proprietary or Metadata does not exist
   */
  public String transform() {
    var dateOptional = Optional.ofNullable(akomaNtoso.getDoc())
      .map(d -> d.getMeta())
      .map(m -> m.getProprietary())
      .map(p -> p.getMetadata())
      .map(m -> m.getEntryIntoEffectDate());

    if (dateOptional.isPresent())
      return dateOptional.get();

    else
      return null;
  }
}
