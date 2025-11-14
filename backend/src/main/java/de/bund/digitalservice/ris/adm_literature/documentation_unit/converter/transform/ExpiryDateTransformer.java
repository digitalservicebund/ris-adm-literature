package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisMeta;
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
      .map(Proprietary::getMeta)
      .map(RisMeta::getExpiryDate)
      .orElse(null);
  }
}
