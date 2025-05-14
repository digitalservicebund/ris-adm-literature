package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.data.domain.Page;

/**
 * Input port for lookup tables.
 */
public interface LookupTablesPort {
  Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query);

  List<Sachgebiet> findFieldsOfLawChildren(@Nonnull String identifier);

  List<Sachgebiet> findFieldsOfLawParents();

  Optional<Sachgebiet> findFieldOfLaw(@Nonnull String identifier);

  Page<Sachgebiet> findFieldsOfLaw(@Nonnull FieldOfLawQuery query);

  Page<LegalPeriodical> findLegalPeriodicals(@Nonnull LegalPeriodicalQuery query);

  Page<Region> findRegions(@Nonnull RegionQuery regionQuery);

  Page<Institution> findInstitutions(@Nonnull InstitutionQuery query);
}
