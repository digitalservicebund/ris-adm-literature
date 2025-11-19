package de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for lookup table 'Zitierart'.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ZitierArtService {

  private final CitationTypeRepository citationTypeRepository;

  /**
   * Finds a paginated list of citation types (ZitierArten) based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link ZitierArt}.
   */
  @Transactional(readOnly = true)
  public Page<ZitierArt> findZitierArten(@Nonnull ZitierArtQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var citationTypes = StringUtils.isBlank(searchTerm)
      ? citationTypeRepository.findAll(pageable)
      : citationTypeRepository.findByDocumentCategoryAndAbbreviationContainingIgnoreCaseOrLabelContainingIgnoreCase(
        query.documentCategory(),
        searchTerm,
        searchTerm,
        pageable
      );
    return PageTransformer.transform(citationTypes, mapCitationTypeEntity());
  }

  /**
   * Finds all citation types (ZitierArten) matching a given abbreviation.
   *
   * @param abbreviation The exact abbreviation to search for
   * @param documentCategory The document category to filter for
   * @return A list of matching {@link ZitierArt}.
   */
  @Transactional(readOnly = true)
  public List<ZitierArt> findZitierArtenByAbbreviation(
    @Nonnull String abbreviation,
    @Nonnull DocumentCategory documentCategory
  ) {
    CitationTypeEntity probe = new CitationTypeEntity();
    probe.setAbbreviation(abbreviation);
    probe.setDocumentCategory(documentCategory);
    return citationTypeRepository
      .findAll(Example.of(probe))
      .stream()
      .map(mapCitationTypeEntity())
      .toList();
  }

  private Function<CitationTypeEntity, ZitierArt> mapCitationTypeEntity() {
    return citationTypeEntity ->
      new ZitierArt(
        citationTypeEntity.getId(),
        citationTypeEntity.getAbbreviation(),
        citationTypeEntity.getLabel()
      );
  }
}
