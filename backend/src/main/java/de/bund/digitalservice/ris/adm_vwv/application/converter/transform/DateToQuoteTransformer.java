package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Transformer for date to quote (in German 'Zitierdatum').
 */
@RequiredArgsConstructor
public class DateToQuoteTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a date to quote string.
   *
   * @return The (first) date to quote, or {@code null} if the surrounding {@code <proprietary>}
   *         or {@code <dateToQuoteList>} elements are {@code null}
   */
  public String transform() {
    Proprietary proprietary = akomaNtoso.getDoc().getMeta().getProprietary();
    if (proprietary == null) {
      return null;
    }
    return Optional.ofNullable(proprietary.getMetadata().getDateToQuoteList())
      // For now, we only deliver the very first item. Handling of multiple waits for RISDEV-6451.
      .map(List::getFirst)
      .orElse(null);
  }
}
