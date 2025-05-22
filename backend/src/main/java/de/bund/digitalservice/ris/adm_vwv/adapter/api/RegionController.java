package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPort;
import de.bund.digitalservice.ris.adm_vwv.application.QueryOptions;
import de.bund.digitalservice.ris.adm_vwv.application.Region;
import de.bund.digitalservice.ris.adm_vwv.application.RegionQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for accessing the {@link Region} resource (lookup table).
 */
@RestController
@RequiredArgsConstructor
public class RegionController {

  private final LookupTablesPort lookupTablesPort;

  /**
   * Return regions (optionally with search term, pagination, sorting)
   *
   * @param searchTerm Keyword to restrict results to.
   * @param pageNumber Which page of pagination to return?
   * @param pageSize How many elements per page in pagination?
   * @param sortByProperty Sort by what property?
   * @param sortDirection Sort ascending or descending?
   * @param usePagination Search with pagination?
   *
   * @return Response object with list of regions and pagination information
   */
  @GetMapping("api/lookup-tables/regions")
  public ResponseEntity<RegionResponse> getRegions(
    @RequestParam(required = false) String searchTerm,
    @RequestParam(defaultValue = "0") int pageNumber,
    @RequestParam(defaultValue = "10") int pageSize,
    @RequestParam(defaultValue = "code") String sortByProperty,
    @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
    @RequestParam(defaultValue = "true") boolean usePagination
  ) {
    QueryOptions queryOptions = new QueryOptions(
      pageNumber,
      pageSize,
      sortByProperty,
      sortDirection,
      usePagination
    );
    var paginatedRegions = lookupTablesPort.findRegions(new RegionQuery(searchTerm, queryOptions));
    return ResponseEntity.ok(
      new RegionResponse(paginatedRegions.content(), new PageResponse(paginatedRegions))
    );
  }
}
