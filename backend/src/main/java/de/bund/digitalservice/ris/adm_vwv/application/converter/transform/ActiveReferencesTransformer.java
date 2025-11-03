package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.ActiveReference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.NormAbbreviation;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.SingleNorm;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisActiveReference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisMeta;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Transformer for active references (in German 'Aktivverweise').
 */
@RequiredArgsConstructor
@Slf4j
public class ActiveReferencesTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of active references.
   *
   * @return Active references list, or an empty list if the surrounding {@code <proprietary>} or
   *         {@code <ris:activeReferences> } elements are {@code null}
   */
  public List<ActiveReference> transform() {
    List<RisActiveReference> risActiveReferences = Optional.ofNullable(
      akomaNtoso.getDoc().getMeta().getProprietary()
    )
      .map(Proprietary::getMeta)
      .map(RisMeta::getActiveReferences)
      .orElse(List.of());
    return risActiveReferences
      .stream()
      .map(ar ->
        new ActiveReference(
          transformReferenceDocumentType(ar),
          transformTypeNumber(ar.getTypeNumber()),
          new NormAbbreviation(null, ar.getReference(), null),
          ar.getReference(),
          List.of(new SingleNorm(null, transformSingleNorm(ar), ar.getDateOfVersion(), null))
        )
      )
      .toList();
  }

  private String transformSingleNorm(RisActiveReference ar) {
    return (ar.getParagraph() + " " + ar.getPosition()).trim();
  }

  private String transformReferenceDocumentType(RisActiveReference risActiveReference) {
    // Implementation needed, to be defined in RISDEV-7812
    log.debug(
      "Implementation missing for determining reference document type for {}. " +
      "Set to 'administrative_regulation'.",
      risActiveReference
    );
    return "administrative_regulation";
  }

  private String transformTypeNumber(String typeNumber) {
    return switch (typeNumber) {
      case "01" -> "anwendung";
      case "31" -> "neuregelung";
      case "82" -> "rechtsgrundlage";
      default -> {
        log.debug("Unhandled type number: {}", typeNumber);
        yield typeNumber;
      }
    };
  }
}
