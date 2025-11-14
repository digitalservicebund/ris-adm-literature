package de.bund.digitalservice.ris.adm_literature.application.converter.transform;

import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.Proprietary;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Transformer for date to quote (in German 'Zitierdatum').
 */
@RequiredArgsConstructor
public class DateToQuoteTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of dates to quote string.
   *
   * @return The list of dates to quote, or and empty list if the surrounding
   *         {@code <proprietary>} or {@code <dateToQuoteList>} elements are {@code null}
   */
  public List<String> transform() {
    Proprietary proprietary = akomaNtoso.getDoc().getMeta().getProprietary();
    if (proprietary == null || proprietary.getMeta().getDateToQuoteList() == null) {
      return List.of();
    }
    return proprietary.getMeta().getDateToQuoteList();
  }
}
