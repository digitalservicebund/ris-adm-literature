package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Reference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Analysis;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.ImplicitReferenceType;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.OtherReferences;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * Transformer for 'Fundstellen'.
 */
@RequiredArgsConstructor
public class FundstellenTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of references.
   *
   * @return Reference list, or an empty list if the surrounding {@code <analysis>} element is {@code null}
   */
  public List<Reference> transform() {
    Analysis analysis = akomaNtoso.getDoc().getMeta().getAnalysis();
    if (analysis == null) {
      return List.of();
    }
    List<OtherReferences> otherReferences = analysis.getOtherReferences();
    return otherReferences
      .stream()
      .map(OtherReferences::getImplicitReference)
      .filter(ir -> ir.getReferenceType() == ImplicitReferenceType.FUNDSTELLE)
      .map(ir -> new Reference(UUID.randomUUID(), ir.getShowAs(), null, ir.getShortForm()))
      .toList();
  }
}
