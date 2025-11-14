package de.bund.digitalservice.ris.adm_literature.application.converter.transform;

import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.RisMeta;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Reference numbers transformer.
 */
@RequiredArgsConstructor
public class ReferenceNumbersTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a string list of reference numbers.
   *
   * @return The list of reference numbers or an empty list
   */
  public List<String> transform() {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMeta)
      .map(RisMeta::getReferenceNumbers)
      .orElse(List.of());
  }
}
