package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LookupTablesService implements LookupTablesPort {

  private final LookupTablesPersistencePort lookupTablesPersistencePort;

  @Override
  public Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query) {
    return lookupTablesPersistencePort.findDocumentTypes(query);
  }

  @Override
  public List<FieldOfLaw> findChildrenOfFieldOfLaw(String identifier) {
    return lookupTablesPersistencePort.findChildrenOfFieldOfLaw(identifier);
  }
}
