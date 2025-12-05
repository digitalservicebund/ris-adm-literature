package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.RisMeta;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * EntryIntoEffectDate transformer.
 */
@RequiredArgsConstructor
public class EntryIntoEffectDateTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code EntryIntoEffectDate} object to an entryIntoEffectDate string.
   *
   * @return The entryIntoEffectDate or null if Proprietary or Metadata does not exist
   */
  public String transform() {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMeta)
      .map(RisMeta::getEntryIntoEffectDate)
      .orElse(null);
  }
}
