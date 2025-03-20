package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.*;

import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LookupTablesPersistenceService implements LookupTablesPersistencePort {
  private static final String ROOT_ID = "root";

  private final DocumentTypesRepository documentTypesRepository;
  private final FieldOfLawRepository fieldOfLawRepository;

  @Override
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

  public List<FieldOfLaw> findChildrenOfFieldOfLaw(String identifier) {
    if (identifier.equalsIgnoreCase(ROOT_ID)) {
      return fieldOfLawRepository.findByParentIsNullAndNotationOrderByIdentifier("NEW").stream()
        .map(fieldOfLawEntity -> FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, false, true))
        .toList();
    }

    return fieldOfLawRepository.findByIdentifier(identifier)
      .map(FieldOfLawEntity::getChildren)
      .map(fieldOfLawEntities -> fieldOfLawEntities.stream().map( fieldOfLawEntity -> FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, false, true)).toList())
      .orElse(List.of());
  }
}
