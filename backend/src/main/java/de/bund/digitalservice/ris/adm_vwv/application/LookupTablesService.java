package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Application service for lookup tables.
 */
@Service
@RequiredArgsConstructor
public class LookupTablesService implements LookupTablesPort {

  private final LookupTablesPersistencePort lookupTablesPersistencePort;

  @Override
  public Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query) {
    return lookupTablesPersistencePort.findDocumentTypes(query);
  }

  @Override
  public List<FieldOfLaw> findFieldsOfLawChildren(@Nonnull String identifier) {
    return lookupTablesPersistencePort.findFieldsOfLawChildren(identifier);
  }

  @Override
  public List<FieldOfLaw> findFieldsOfLawParents() {
    return lookupTablesPersistencePort.findFieldsOfLawParents();
  }

  @Override
  public Optional<FieldOfLaw> findFieldOfLaw(@Nonnull String identifier) {
    return lookupTablesPersistencePort.findFieldOfLaw(identifier);
  }

  @Override
  public Page<FieldOfLaw> findFieldsOfLaw(@NotNull FieldOfLawQuery query) {
    return lookupTablesPersistencePort.findFieldsOfLaw(query);
  }

  @Override
  public Page<LegalPeriodical> findLegalPeriodicals(@NotNull LegalPeriodicalQuery query) {
    return lookupTablesPersistencePort.findLegalPeriodicals(query);
  }
}
