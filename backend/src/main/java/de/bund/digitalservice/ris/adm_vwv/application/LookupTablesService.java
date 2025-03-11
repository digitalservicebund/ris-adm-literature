package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LookupTablesService implements LookupTablesPort {

  private final LookupTablesPersistencePort lookupTablesPersistencePort;

  @Override
  public List<DocumentType> findBySearchQuery(String searchQuery) {
    return lookupTablesPersistencePort.findBySearchQuery(searchQuery);
  }
}
