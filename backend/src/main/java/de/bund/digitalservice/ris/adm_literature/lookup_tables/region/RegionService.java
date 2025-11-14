package de.bund.digitalservice.ris.adm_literature.lookup_tables.region;

import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.Optional;
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
 * Service for lookup table region.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegionService {

  private final RegionRepository regionRepository;

  /**
   * Finds a paginated list of regions based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link Region}.
   */
  @Transactional(readOnly = true)
  public Page<Region> findRegions(@Nonnull RegionQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var regions = StringUtils.isBlank(searchTerm)
      ? regionRepository.findAll(pageable)
      : regionRepository.findByCodeContainingIgnoreCase(searchTerm, pageable);

    return PageTransformer.transform(regions, mapRegionEntity());
  }

  /**
   * Finds a single region by its code.
   *
   * @param code The code of the region to find.
   * @return An {@link Optional} containing the found {@link Region}, or empty if not found.
   */
  @Transactional(readOnly = true)
  public Optional<Region> findRegionByCode(@Nonnull String code) {
    return regionRepository.findByCode(code).map(mapRegionEntity());
  }

  private Function<RegionEntity, Region> mapRegionEntity() {
    return regionEntity ->
      new Region(regionEntity.getId(), regionEntity.getCode(), regionEntity.getLongText());
  }
}
