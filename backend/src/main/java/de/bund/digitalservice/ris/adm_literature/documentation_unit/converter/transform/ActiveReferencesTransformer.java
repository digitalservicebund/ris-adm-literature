package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.ActiveReference;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.SingleNorm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisActiveReference;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisMeta;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.norm_abbreviation.NormAbbreviation;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp.VerweisTyp;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp.VerweisTypService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Transformer for active references (in German 'Aktivverweise').
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class ActiveReferencesTransformer {

  private final VerweisTypService verweisTypService;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of active references.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return Active references list, or an empty list if the surrounding {@code <proprietary>} or
   *         {@code <ris:activeReferences> } elements are {@code null}
   */
  public List<ActiveReference> transform(AkomaNtoso akomaNtoso) {
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

  private VerweisTyp transformTypeNumber(String typeNumber) {
    return verweisTypService
      .findVerweisTypByTypNummer(typeNumber)
      .orElse(new VerweisTyp(UUID.randomUUID(), "", typeNumber));
  }
}
