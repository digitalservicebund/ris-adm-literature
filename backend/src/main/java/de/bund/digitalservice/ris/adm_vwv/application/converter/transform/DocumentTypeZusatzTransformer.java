package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisDocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisMetadata;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Transformer for date to quote (in German 'Zitierdatum').
 */
@RequiredArgsConstructor
public class DocumentTypeZusatzTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a date to quote string.
   *
   * @return The (first) date to quote, or {@code null} if the surrounding {@code <proprietary>}
   *         or {@code <dateToQuoteList>} elements are {@code null}
   */
  public String transform() {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary().getMetadata())
      .map(RisMetadata::getDocumentType)
      .map(RisDocumentType::getLongTitle)
      .orElse(null);
  }
}
