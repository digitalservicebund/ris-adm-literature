package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisMetadata;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Expiry date transformer.
 */
@RequiredArgsConstructor
public class ExpiryDateTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code ExpiryDate} object to an expiryDate string.
   *
   * @return The expiryDate or null if Proprietary or Metadata does not exist
   */
  public String transform() {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMetadata)
      .map(RisMetadata::getExpiryDate)
      .orElse(null);
  }
}
