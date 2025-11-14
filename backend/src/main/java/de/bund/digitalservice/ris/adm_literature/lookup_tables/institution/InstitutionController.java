package de.bund.digitalservice.ris.adm_literature.lookup_tables.institution;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for accessing the {@link Institution} resource (lookup table).
 */
@RestController
@RequiredArgsConstructor
public class InstitutionController {

  private final InstitutionService institutionService;

  /**
   * Return institutions (optionally with search term, pagination, sorting)
   *
   * @param searchTerm Keyword to restrict results to.
   * @param pageNumber Which page of pagination to return?
   * @param pageSize How many elements per page in pagination?
   * @param sortByProperty Sort by what property?
   * @param sortDirection Sort ascending or descending?
   * @param usePagination Search with pagination?
   *
   * @return Response object with list of institutions and pagination information
   */
  @GetMapping("api/lookup-tables/institutions")
  public ResponseEntity<InstitutionResponse> getInstitutions(
    @RequestParam(required = false) String searchTerm,
    @RequestParam(defaultValue = "0") int pageNumber,
    @RequestParam(defaultValue = "10") int pageSize,
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
    var paginatedInstitutions = institutionService.findInstitutions(
      new InstitutionQuery(searchTerm, queryOptions)
    );
    return ResponseEntity.ok(
      new InstitutionResponse(
        paginatedInstitutions.content(),
        new PageResponse(paginatedInstitutions)
      )
    );
  }
}
