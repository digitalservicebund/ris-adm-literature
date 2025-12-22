package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for searching adm references (documentation units) to be used as 'Aktivzitierungen'.
 */
@RestController
@RequiredArgsConstructor
public class AdmAktivzitierungController {

  private final AdmReferenceService admReferenceService;

  /**
   * Returns administrative citations (Aktivzitierungen) overview from the ADM schema.
   *
   * @param documentNumber The document number to search for.
   * @param periodikum The periodikum to search for.
   * @param zitatstelle The zitatstelle to search for.
   * @param inkrafttretedatum The effective date to search for.
   * @param aktenzeichen The docket or reference number to search for.
   * @param dokumenttyp The document type to search for.
   * @param normgeber The issuing authority to search for.
   * @param zitierdatum The 'Zitierdatum' to search for
   * @param pageNumber The page number of the result set.
   * @param pageSize The page size of the result set.
   * @param sortByProperty The property to sort by.
   * @param sortDirection The sort direction.
   *
   * @return A response containing a paginated list of administrative overview elements.
   */
  @GetMapping("api/literature/aktivzitierungen/adm")
  public ResponseEntity<AdmAktivzitierungResults> findAktivzitierungen(
    @RequestParam(required = false) String documentNumber,
    @RequestParam(required = false) String periodikum,
    @RequestParam(required = false) String zitatstelle,
    @RequestParam(required = false) String inkrafttretedatum,
    @RequestParam(required = false) String aktenzeichen,
    @RequestParam(required = false) String dokumenttyp,
    @RequestParam(required = false) String normgeber,
    @RequestParam(required = false) String zitierdatum,
    @RequestParam(defaultValue = "0") int pageNumber,
    @RequestParam(defaultValue = "15") int pageSize,
    @RequestParam(defaultValue = "documentNumber") String sortByProperty,
    @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection
  ) {
    QueryOptions queryOptions = new QueryOptions(
      pageNumber,
      pageSize,
      sortByProperty,
      sortDirection,
      true
    );

    var paginatedResults = admReferenceService.findAktivzitierungen(
      new AdmAktivzitierungQuery(
        StringUtils.trimToNull(documentNumber),
        StringUtils.trimToNull(periodikum),
        StringUtils.trimToNull(zitatstelle),
        StringUtils.trimToNull(inkrafttretedatum),
        StringUtils.trimToNull(aktenzeichen),
        StringUtils.trimToNull(dokumenttyp),
        StringUtils.trimToNull(normgeber),
        StringUtils.trimToNull(zitierdatum),
        queryOptions
      )
    );

    return ResponseEntity.ok(
      new AdmAktivzitierungResults(paginatedResults.content(), new PageResponse(paginatedResults))
    );
  }
}
