package de.bund.digitalservice.ris.adm_literature.lookup_tables.court;

import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for court lookup tables.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CourtService {

  private final CourtRepository courtRepository;

  /**
   * Finds a paginated list of courts based on type or location.
   *
   * @param query The query containing search terms and pagination info.
   * @return A page of {@link Court} DTOs.
   */
  @Transactional(readOnly = true)
  public Page<Court> findCourts(@Nonnull CourtQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var courts = StringUtils.isBlank(searchTerm)
      ? courtRepository.findAll(pageable)
      : courtRepository.findByTypeContainingIgnoreCaseOrLocationContainingIgnoreCase(
        searchTerm,
        searchTerm,
        pageable
      );

    return PageTransformer.transform(courts, mapCourtEntity());
  }

  private Function<CourtEntity, Court> mapCourtEntity() {
    return courtEntity ->
      new Court(courtEntity.getId(), courtEntity.getType(), courtEntity.getLocation());
  }
}
