package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.Definition;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.RisDefinition;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.RisMeta;
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
