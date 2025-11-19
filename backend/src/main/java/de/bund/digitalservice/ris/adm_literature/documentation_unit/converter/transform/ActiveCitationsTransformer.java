package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.ActiveCitation;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.*;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.court.Court;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart.ZitierArt;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart.ZitierArtService;
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

  private final ZitierArtService zitierArtService;

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
    return zitierArtService
      .findZitierArtenByAbbreviation(
        caselawReference.getAbbreviation(),
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN
      )
      .stream()
      .findFirst()
      .orElse(
        new ZitierArt(null, caselawReference.getAbbreviation(), caselawReference.getAbbreviation())
      );
  }
}
