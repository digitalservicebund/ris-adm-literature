package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.LegalPeriodical;
import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPersistencePort;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Reference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Analysis;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.ImplicitReferenceType;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.OtherReferences;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Transformer for 'Fundstellen'.
 */
@RequiredArgsConstructor
@Component
public class FundstellenTransformer {

  private final LookupTablesPersistencePort lookupTablesPersistencePort;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of references.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return Reference list, or an empty list if the surrounding {@code <analysis>} element is {@code null}
   */
  public List<Reference> transform(@Nonnull AkomaNtoso akomaNtoso) {
    Analysis analysis = akomaNtoso.getDoc().getMeta().getAnalysis();
    if (analysis == null) {
      return List.of();
    }
    List<OtherReferences> otherReferences = analysis.getOtherReferences();
    return otherReferences
      .stream()
      .map(OtherReferences::getImplicitReference)
      .filter(ir -> ir.getReferenceType() == ImplicitReferenceType.FUNDSTELLE)
      .map(ir -> {
        String abbreviation = ir.getShortForm();
        LegalPeriodical legalPeriodical = findLegalPeriodical(abbreviation);
        // In case the legal periodical is not unique or not existing, the raw value is set and displayed in the UI.
        return new Reference(UUID.randomUUID(), ir.getShowAs(), legalPeriodical, abbreviation);
      })
      .toList();
  }

  @Nullable
  private LegalPeriodical findLegalPeriodical(String abbreviation) {
    LegalPeriodical legalPeriodical = null;
    List<LegalPeriodical> legalPeriodicals =
      lookupTablesPersistencePort.findLegalPeriodicalsByAbbreviation(abbreviation);
    if (legalPeriodicals.size() == 1) {
      // The legal periodical is only set, if it is found and unambiguous in the database. In case there are multiple
      // legal periodicals with the same abbreviation it is not set which results to a user hint in
      // the UI ("Mehrdeutiger Verweis"). This behaviour is the same as in Caselaw.
      legalPeriodical = legalPeriodicals.getFirst();
    }
    return legalPeriodical;
  }
}
