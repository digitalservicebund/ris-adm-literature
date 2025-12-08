package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.RisMeta;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Berufsbilder transformer.
 */
@RequiredArgsConstructor
public class BerufsbilderTransformer {

  /**
   * Transforms the {@code AkomaNtoso} object to a list of berufsbild represented as strings.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return The list of berufsbild (empty if there are none)
   */
  public List<String> transform(@Nonnull AkomaNtoso akomaNtoso) {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMeta)
      .map(RisMeta::getBerufsbilder)
      .orElse(List.of());
  }
}
