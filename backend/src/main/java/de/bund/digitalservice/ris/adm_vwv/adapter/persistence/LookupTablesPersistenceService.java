package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPersistencePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LookupTablesPersistenceService implements LookupTablesPersistencePort {

  private final DocumentTypesRepository documentTypesRepository;

  @Override
  public List<DocumentType> findBySearchQuery(String searchQuery) {
    List<DocumentTypeEntity> documentTypes = StringUtils.isBlank(searchQuery)
      ? documentTypesRepository.findAll()
      : documentTypesRepository.findByAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
        searchQuery,
        searchQuery
      );

    return documentTypes
      .stream()
      .map(x -> new DocumentType(x.getAbbreviation(), x.getName()))
      .toList();
  }
}
