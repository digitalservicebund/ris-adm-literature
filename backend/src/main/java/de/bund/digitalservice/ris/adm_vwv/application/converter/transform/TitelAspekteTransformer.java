package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisMetadata;
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
      .map(Proprietary::getMetadata)
      .map(RisMetadata::getTitelAspekte)
      .orElse(List.of());
  }
}
