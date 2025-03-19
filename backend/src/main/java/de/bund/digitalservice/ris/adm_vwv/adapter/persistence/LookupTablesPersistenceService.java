package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentTypeQuery;
import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPersistencePort;
import de.bund.digitalservice.ris.adm_vwv.application.PageQuery;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LookupTablesPersistenceService implements LookupTablesPersistencePort {

  private final DocumentTypesRepository documentTypesRepository;

  @Override
  public List<DocumentType> findBySearchQuery(@Nonnull DocumentTypeQuery query) {
    PageQuery pageQuery = query.pageQuery();
    String searchQuery = query.searchQuery();
    Pageable pageable = PageRequest.of(
      pageQuery.page(),
      pageQuery.size(),
      Sort.by(pageQuery.sortDirection(), pageQuery.sortBy())
    );
    Page<DocumentTypeEntity> documentTypes = StringUtils.isBlank(searchQuery)
      ? documentTypesRepository.findAll(pageable)
      : documentTypesRepository.findByAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
        searchQuery,
        searchQuery,
        pageable
      );

    return documentTypes
      .stream()
      .map(documentTypeEntity ->
        new DocumentType(documentTypeEntity.getAbbreviation(), documentTypeEntity.getName())
      )
      .toList();
  }
}
