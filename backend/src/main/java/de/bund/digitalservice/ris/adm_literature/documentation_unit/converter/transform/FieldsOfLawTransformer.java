package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisMeta;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law.FieldOfLaw;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law.FieldOfLawService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Fields of law transformer.
 */
@Component
@RequiredArgsConstructor
public class FieldsOfLawTransformer {

  private final FieldOfLawService fieldOfLawService;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of fields of law.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return The fields of law or an empty list if the surrounding {@code <proprietary>}
   *         or {@code ris:fieldsOfLaw} elements are {@code null}
   */
  public List<FieldOfLaw> transform(AkomaNtoso akomaNtoso) {
    var fieldsOfLaw = Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMeta)
      .map(RisMeta::getFieldsOfLaw)
      .orElse(List.of());
    return fieldsOfLaw
      .stream()
      .map(risFieldOfLaw ->
        fieldOfLawService
          .findFieldOfLaw(risFieldOfLaw.getValue())
          .orElseGet(() ->
            new FieldOfLaw(
              UUID.randomUUID(),
              false,
              risFieldOfLaw.getValue(),
              risFieldOfLaw.getValue(),
              risFieldOfLaw.getNotation(),
              List.of(),
              List.of(),
              List.of(),
              null
            )
          )
      )
      .toList();
  }
}
