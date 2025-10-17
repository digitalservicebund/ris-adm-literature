package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.*;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.VerweisTyp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for accessing the {@link VerweisTyp} resource (lookup table).
 */
@RestController
@RequiredArgsConstructor
public class VerweisTypController {

  private final LookupTablesService lookupTablesService;

  /**
   * Return verweisTypen (optionally with search term, pagination, sorting)
   *
   * @param searchTerm Keyword to restrict results to.
   * @param pageNumber Which page of pagination to return?
   * @param pageSize How many elements per page in pagination?
   * @param sortByProperty Sort by what property?
   * @param sortDirection Sort ascending or descending?
   * @param usePagination Search with pagination?
   *
   * @return Response object with list of verweisTypen and pagination information
   */
  @GetMapping("api/lookup-tables/verweis-typen")
  public ResponseEntity<VerweisTypResponse> getVerweisTypen(
    @RequestParam(required = false) String searchTerm,
    @RequestParam(defaultValue = "0") int pageNumber,
    @RequestParam(defaultValue = "3") int pageSize,
    @RequestParam(defaultValue = "name") String sortByProperty,
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
    var paginatedVerweisTypen = lookupTablesService.findVerweisTypen(
      new VerweisTypQuery(searchTerm, queryOptions)
    );
    return ResponseEntity.ok(
      new VerweisTypResponse(
        paginatedVerweisTypen.content(),
        new PageResponse(paginatedVerweisTypen)
      )
    );
  }
}
