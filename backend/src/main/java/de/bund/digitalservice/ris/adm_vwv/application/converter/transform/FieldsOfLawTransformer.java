package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPersistencePort;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisMetadata;
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

  private final LookupTablesPersistencePort lookupTablesPersistencePort;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of fields of law.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return The fields of law or an empty list if the surrounding {@code <proprietary>}
   *         or {@code ris:fieldsOfLaw} elements are {@code null}
   */
  public List<FieldOfLaw> transform(AkomaNtoso akomaNtoso) {
    var fieldsOfLaw = Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMetadata)
      .map(RisMetadata::getFieldsOfLaw)
      .orElse(List.of());
    return fieldsOfLaw
      .stream()
      .map(risFieldOfLaw ->
        lookupTablesPersistencePort
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
