package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisMeta;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * TitelAspekte transformer.
 */
@RequiredArgsConstructor
public class TitelAspekteTransformer {

  /**
   * Transforms the {@code AkomaNtoso} object to a list of titelAspekt represented as strings.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return The list of titelAspekt (empty if there are none)
   */
  public List<String> transform(@Nonnull AkomaNtoso akomaNtoso) {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMeta)
      .map(RisMeta::getTitelAspekte)
      .orElse(List.of());
  }
}
