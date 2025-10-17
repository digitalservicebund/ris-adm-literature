package de.bund.digitalservice.ris.adm_vwv.application;

import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.LookupTablesPersistenceService;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Court;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.NormAbbreviation;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.VerweisTyp;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service for lookup tables.
 */
@Service
@RequiredArgsConstructor
public class LookupTablesService {

  private final LookupTablesPersistenceService lookupTablesPersistenceService;

  public Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query) {
    return lookupTablesPersistenceService.findDocumentTypes(query);
  }

  public List<FieldOfLaw> findFieldsOfLawChildren(@Nonnull String identifier) {
    return lookupTablesPersistenceService.findFieldsOfLawChildren(identifier);
  }

  public List<FieldOfLaw> findFieldsOfLawParents() {
    return lookupTablesPersistenceService.findFieldsOfLawParents();
  }

  public Optional<FieldOfLaw> findFieldOfLaw(@Nonnull String identifier) {
    return lookupTablesPersistenceService.findFieldOfLaw(identifier);
  }

  public Page<FieldOfLaw> findFieldsOfLaw(@Nonnull FieldOfLawQuery query) {
    return lookupTablesPersistenceService.findFieldsOfLaw(query);
  }

  public Page<LegalPeriodical> findLegalPeriodicals(@Nonnull LegalPeriodicalQuery query) {
    return lookupTablesPersistenceService.findLegalPeriodicals(query);
  }

  public Page<Region> findRegions(@Nonnull RegionQuery regionQuery) {
    return lookupTablesPersistenceService.findRegions(regionQuery);
  }

  public Page<Institution> findInstitutions(@Nonnull InstitutionQuery query) {
    return lookupTablesPersistenceService.findInstitutions(query);
  }

  public Page<Court> findCourts(@Nonnull CourtQuery query) {
    return lookupTablesPersistenceService.findCourts(query);
  }

  public Page<ZitierArt> findZitierArten(@Nonnull ZitierArtQuery query) {
    return lookupTablesPersistenceService.findZitierArten(query);
  }

  public Page<NormAbbreviation> findNormAbbreviations(@Nonnull NormAbbreviationQuery query) {
    return lookupTablesPersistenceService.findNormAbbreviations(query);
  }

  public Page<VerweisTyp> findVerweisTypen(@Nonnull VerweisTypQuery query) {
    return lookupTablesPersistenceService.findVerweisTypen(query);
  }
}
