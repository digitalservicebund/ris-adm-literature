package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Definition;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisDefinition;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisMeta;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Definitionen transformer.
 */
@RequiredArgsConstructor
public class DefinitionenTransformer {

  /**
   * Transforms the {@code AkomaNtoso} object to a list of definitions.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return The list of definitions (empty if there are none)
   */
  public List<Definition> transform(@Nonnull AkomaNtoso akomaNtoso) {
    List<RisDefinition> risDefinitionen = Optional.ofNullable(
      akomaNtoso.getDoc().getMeta().getProprietary()
    )
      .map(Proprietary::getMeta)
      .map(RisMeta::getDefinitionen)
      .orElse(List.of());

    return risDefinitionen
      .stream()
      .map(definition -> new Definition(definition.getBegriff()))
      .toList();
  }
}
