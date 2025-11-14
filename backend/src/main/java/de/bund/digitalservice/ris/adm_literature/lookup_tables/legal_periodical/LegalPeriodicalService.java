package de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical;

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
 * Service for lookup table legal periodical (in German 'Periodikum').
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LegalPeriodicalService {

  private final LegalPeriodicalsRepository legalPeriodicalsRepository;

  /**
   * Finds a paginated list of legal periodicals based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link LegalPeriodical}.
   */
  @Transactional(readOnly = true)
  public Page<LegalPeriodical> findLegalPeriodicals(@Nonnull LegalPeriodicalQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var legalPeriodicals = StringUtils.isBlank(searchTerm)
      ? legalPeriodicalsRepository.findAll(pageable)
      : legalPeriodicalsRepository.findByAbbreviationContainingIgnoreCaseOrTitleContainingIgnoreCase(
        searchTerm,
        searchTerm,
        pageable
      );

    return PageTransformer.transform(legalPeriodicals, mapLegalPeriodicalEntity());
  }

  private Function<LegalPeriodicalEntity, LegalPeriodical> mapLegalPeriodicalEntity() {
    return legalPeriodicalEntity ->
      new LegalPeriodical(
        legalPeriodicalEntity.getId(),
        legalPeriodicalEntity.getAbbreviation(),
        legalPeriodicalEntity.getPublicId(),
        legalPeriodicalEntity.getTitle(),
        legalPeriodicalEntity.getSubtitle(),
        legalPeriodicalEntity.getCitationStyle()
      );
  }

  /**
   * Finds all legal periodicals matching a given abbreviation.
   *
   * @param abbreviation The exact abbreviation to search for.
   * @return A list of matching {@link LegalPeriodical}.
   */
  @Transactional(readOnly = true)
  public List<LegalPeriodical> findLegalPeriodicalsByAbbreviation(@Nonnull String abbreviation) {
    LegalPeriodicalEntity probe = new LegalPeriodicalEntity();
    probe.setAbbreviation(abbreviation);
    return legalPeriodicalsRepository
      .findAll(Example.of(probe))
      .stream()
      .map(mapLegalPeriodicalEntity())
      .toList();
  }
}
