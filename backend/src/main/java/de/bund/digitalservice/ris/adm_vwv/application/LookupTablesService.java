package de.bund.digitalservice.ris.adm_vwv.application;

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

  private final LookupTablesPersistencePort lookupTablesPersistencePort;

  public Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query) {
    return lookupTablesPersistencePort.findDocumentTypes(query);
  }

  public List<FieldOfLaw> findFieldsOfLawChildren(@Nonnull String identifier) {
    return lookupTablesPersistencePort.findFieldsOfLawChildren(identifier);
  }

  public List<FieldOfLaw> findFieldsOfLawParents() {
    return lookupTablesPersistencePort.findFieldsOfLawParents();
  }

  public Optional<FieldOfLaw> findFieldOfLaw(@Nonnull String identifier) {
    return lookupTablesPersistencePort.findFieldOfLaw(identifier);
  }

  public Page<FieldOfLaw> findFieldsOfLaw(@Nonnull FieldOfLawQuery query) {
    return lookupTablesPersistencePort.findFieldsOfLaw(query);
  }

  public Page<LegalPeriodical> findLegalPeriodicals(@Nonnull LegalPeriodicalQuery query) {
    return lookupTablesPersistencePort.findLegalPeriodicals(query);
  }

  public Page<Region> findRegions(@Nonnull RegionQuery regionQuery) {
    return lookupTablesPersistencePort.findRegions(regionQuery);
  }

  public Page<Institution> findInstitutions(@Nonnull InstitutionQuery query) {
    return lookupTablesPersistencePort.findInstitutions(query);
  }

  public Page<Court> findCourts(@Nonnull CourtQuery query) {
    return lookupTablesPersistencePort.findCourts(query);
  }

  public Page<ZitierArt> findZitierArten(@Nonnull ZitierArtQuery query) {
    return lookupTablesPersistencePort.findZitierArten(query);
  }

  public Page<NormAbbreviation> findNormAbbreviations(@Nonnull NormAbbreviationQuery query) {
    return lookupTablesPersistencePort.findNormAbbreviations(query);
  }

  public Page<VerweisTyp> findVerweisTypen(@Nonnull VerweisTypQuery query) {
    return lookupTablesPersistencePort.findVerweisTypen(query);
  }
}
