package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.*;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LookupTablesPersistenceService implements LookupTablesPersistencePort {

  private final DocumentTypesRepository documentTypesRepository;
  private final FieldOfLawRepository fieldOfLawRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query) {
    PageQuery pageQuery = query.pageQuery();
    String searchQuery = query.searchQuery();
    Sort sort = Sort.by(pageQuery.sortDirection(), pageQuery.sortBy());
    Pageable pageable = pageQuery.paged()
      ? PageRequest.of(pageQuery.page(), pageQuery.size(), sort)
      : Pageable.unpaged(sort);
    Page<DocumentTypeEntity> documentTypes = StringUtils.isBlank(searchQuery)
      ? documentTypesRepository.findAll(pageable)
      : documentTypesRepository.findByAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
        searchQuery,
        searchQuery,
        pageable
      );

    return documentTypes.map(documentTypeEntity ->
      new DocumentType(documentTypeEntity.getAbbreviation(), documentTypeEntity.getName())
    );
  }

  @Transactional(readOnly = true)
  @Override
  public List<FieldOfLaw> findFieldsOfLawChildren(@Nonnull String identifier) {
    return fieldOfLawRepository
      .findByIdentifier(identifier)
      .map(fieldOfLawEntity -> FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, true, true)
      )
      .map(FieldOfLaw::children)
      .orElse(List.of());
  }

  @Transactional(readOnly = true)
  @Override
  public List<FieldOfLaw> findFieldsOfLawParents() {
    return fieldOfLawRepository
      .findByParentIsNullAndNotationOrderByIdentifier("NEW")
      .stream()
      .map(fieldOfLawEntity ->
        FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, false, true)
      )
      .toList();
  }
}
