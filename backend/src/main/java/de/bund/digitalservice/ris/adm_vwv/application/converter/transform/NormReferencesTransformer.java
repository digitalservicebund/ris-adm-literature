package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.NormReference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Transformer for norms references (in German 'Normenkette').
 */
@RequiredArgsConstructor
public class NormReferencesTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a date to quote string.
   *
   * @return The (first) date to quote, or {@code null} if the surrounding {@code <proprietary>}
   *         or {@code <dateToQuoteList>} elements are {@code null}
   */
  public List<NormReference> transform() {
    // Proprietary proprietary = akomaNtoso.getDoc().getMeta().getProprietary();
    // if (proprietary == null) {
    //   return null;
    // }
    // return Optional.ofNullable(proprietary.getMetadata().getDateToQuoteList())
    //   // For now, we only deliver the very first item. Handling of multiple waits for RISDEV-6451.
    //   .map(List::getFirst)
    //   .orElse(null);

    return List.of();
  }
}
