package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.LookupTablesPersistenceService;
import de.bund.digitalservice.ris.adm_vwv.application.ZitierArt;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.ActiveCitation;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Court;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Transformer for active citations (in German 'Aktivzitierung
 * Rechtssprechung').
 */
@RequiredArgsConstructor
@Component
public class ActiveCitationsTransformer {

  private final LookupTablesPersistenceService lookupTablesPersistenceService;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of active citations.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return Active citations list, or an empty list if the surrounding
   *         {@code <analysis>} element is {@code null}
   */
  public List<ActiveCitation> transform(@Nonnull AkomaNtoso akomaNtoso) {
    Analysis analysis = akomaNtoso.getDoc().getMeta().getAnalysis();
    if (analysis == null) {
      return List.of();
    }
    List<OtherReferences> otherReferences = analysis.getOtherReferences();
    return otherReferences
      .stream()
      .flatMap(or -> or.getImplicitReferences().stream())
      .filter(ir -> ir.getReferenceType() == ImplicitReferenceType.ACTIVE_CITATION)
      .map(ImplicitReference::getCaselawReference)
      .map(cr ->
        new ActiveCitation(
          UUID.randomUUID(),
          false,
          cr.getDocumentNumber(),
          // Lookup id after implementing RISDEV-6318 (migrate "Courts" lookup table)
          new Court(UUID.randomUUID(), cr.getCourt(), cr.getCourtLocation()),
          cr.getDate(),
          cr.getReferenceNumber(),
          null,
          findZitierArt(cr)
        )
      )
      .toList();
  }

  private ZitierArt findZitierArt(RisCaselawReference caselawReference) {
    return lookupTablesPersistenceService
      .findZitierArtenByAbbreviation(caselawReference.getAbbreviation())
      .stream()
      .findFirst()
      .orElse(new ZitierArt(null, caselawReference.getAbbreviation(), null));
  }
}
