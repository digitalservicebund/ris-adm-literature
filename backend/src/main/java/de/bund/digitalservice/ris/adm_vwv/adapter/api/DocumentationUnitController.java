package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import com.fasterxml.jackson.databind.JsonNode;
import de.bund.digitalservice.ris.adm_vwv.application.*;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for CRUD on documentation units.
 */
@RestController
@RequiredArgsConstructor
public class DocumentationUnitController {

  private static final Set<String> INDEX_ALIASES = Set.of(
    "langueberschrift",
    "fundstellen",
    "zitierdaten"
  );

  private final DocumentationUnitPort documentationUnitPort;

  /**
   * Returns information on all documentation units as required by the
   * documentation units overview.
   *
   * @param documentNumber   Filter by documentNumber.
   * @param langueberschrift Filter by langueberschrift.
   * @param fundstellen      Filter by fundstellen.
   * @param zitierdaten      Filter by zitierdaten.
   * @param pageNumber       Which page of pagination to return?
   * @param pageSize         How many elements per page in pagination?
   * @param sortByProperty   Sort by what property?
   * @param sortDirection    Sort ascending or descending?
   * @param usePagination    Search with pagination?
   * @return Paginated list of document units
   */
  @GetMapping("api/documentation-units")
  public ResponseEntity<DocumentationUnitsOverviewResponse> find(
    @RequestParam(value = "documentNumber", required = false) String documentNumber,
    @RequestParam(value = "langueberschrift", required = false) String langueberschrift,
    @RequestParam(value = "fundstellen", required = false) String fundstellen,
    @RequestParam(value = "zitierdaten", required = false) String zitierdaten,
    @RequestParam(defaultValue = "0") int pageNumber,
    @RequestParam(defaultValue = "10") int pageSize,
    @RequestParam(defaultValue = "documentNumber") String sortByProperty,
    @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
    @RequestParam(defaultValue = "true") boolean usePagination
  ) {
    String resolvedSortByProperty = INDEX_ALIASES.contains(sortByProperty)
      ? "documentationUnitIndex." + sortByProperty
      : sortByProperty;

    QueryOptions queryOptions = new QueryOptions(
      pageNumber,
      pageSize,
      resolvedSortByProperty,
      sortDirection,
      usePagination
    );

    var paginatedDocumentationUnits = documentationUnitPort.findDocumentationUnitOverviewElements(
      new DocumentationUnitQuery(
        StringUtils.trimToNull(documentNumber),
        StringUtils.trimToNull(langueberschrift),
        StringUtils.trimToNull(fundstellen),
        StringUtils.trimToNull(zitierdaten),
        queryOptions
      )
    );
    return ResponseEntity.ok(
      new DocumentationUnitsOverviewResponse(
        paginatedDocumentationUnits.content(),
        new PageResponse(paginatedDocumentationUnits)
      )
    );
  }

  /**
   * Returns a single documentation unit by its document number
   *
   * @param documentNumber The document number of the document unit to be returned
   * @return The document unit or HTTP 404 if not found
   */
  @GetMapping("api/documentation-units/{documentNumber}")
  public ResponseEntity<DocumentationUnit> find(@PathVariable String documentNumber) {
    return documentationUnitPort
      .findByDocumentNumber(documentNumber)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Creates a new documentation unit with a new document number in database and
   * returns it.
   *
   * @return Created documentation unit
   */
  @PostMapping("api/documentation-units")
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentationUnit create() {
    return documentationUnitPort.create();
  }

  /**
   * Updates a documentation unit
   *
   * @param documentNumber    The document number of the document to update
   * @param documentationUnit The JSON of the documentation unit to update
   * @return The updated documentation unit or HTTP 404 if not found
   */
  @PutMapping("api/documentation-units/{documentNumber}")
  public ResponseEntity<DocumentationUnit> update(
    @PathVariable String documentNumber,
    @RequestBody JsonNode documentationUnit
  ) {
    return documentationUnitPort
      .update(documentNumber, documentationUnit.toString())
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Publishes the documentation unit with the given document number and content.
   *
   * @param documentNumber           The document number of the document to publish
   * @param documentationUnitContent The documentation unit content to publish
   * @return The published documentation unit or
   * - HTTP 404 if not found
   * - HTTP 503 if the external
   */
  @PutMapping("api/documentation-units/{documentNumber}/publish")
  public ResponseEntity<DocumentationUnit> publish(
    @PathVariable String documentNumber,
    @RequestBody @Valid DocumentationUnitContent documentationUnitContent
  ) {
    Optional<DocumentationUnit> optionalDocumentationUnit = documentationUnitPort.publish(
      documentNumber,
      documentationUnitContent
    );
    return optionalDocumentationUnit
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }
}
