package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLawQuery;
import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPort;
import de.bund.digitalservice.ris.adm_vwv.application.PageQuery;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Field of law REST controller.
 */
@RestController
@RequiredArgsConstructor
public class FieldOfLawController {

  private final LookupTablesPort lookupTablesPort;

  /**
   * Returns all fields of law under {@literal root} node.
   *
   * @return List of fields of law without parents
   */
  @GetMapping("api/lookup-tables/fields-of-law/root/children")
  public ResponseEntity<FieldOfLawResponse> getFieldsOfLawParents() {
    return ResponseEntity.ok(new FieldOfLawResponse(lookupTablesPort.findFieldsOfLawParents()));
  }

  /**
   * Returns the children fields of law by the given parent identifier.
   *
   * @param identifier Parent identifier
   * @return Children of given parent
   */
  @GetMapping("api/lookup-tables/fields-of-law/{identifier}/children")
  public ResponseEntity<FieldOfLawResponse> getFieldsOfLawChildren(
    @PathVariable String identifier
  ) {
    return ResponseEntity.ok(
      new FieldOfLawResponse(lookupTablesPort.findFieldsOfLawChildren(identifier))
    );
  }

  /**
   * Returns a single field of law by the given identifier.
   *
   * @param identifier Identifier to return
   * @return Found field of law, or HTTP status 404 if not found
   */
  @GetMapping("api/lookup-tables/fields-of-law/{identifier}")
  public ResponseEntity<FieldOfLaw> getTreeForFieldOfLaw(@PathVariable String identifier) {
    return lookupTablesPort
      .findFieldOfLaw(identifier)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Returns a query result for a field of law searched specified by the given parameters.
   *
   * @param identifier The identifier to search for
   * @param text The text term(s) to search for
   * @param norm The norm term(s) to search for
   * @param page The page number
   * @param size Size of page
   * @param sortBy Attribute to sort by
   * @param sortDirection Sort direction
   * @param paged {@code true} if the result have to be paginated, {@code false} otherwise
   * @return Query result
   */
  @GetMapping("api/lookup-tables/fields-of-law")
  public FieldOfLawQueryResponse findFieldsOfLaw(
    @RequestParam(value = "identifier", required = false) String identifier,
    @RequestParam(value = "text", required = false) String text,
    @RequestParam(value = "norm", required = false) String norm,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "30") int size,
    @RequestParam(defaultValue = "identifier") String sortBy,
    @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
    @RequestParam(defaultValue = "true") boolean paged
  ) {
    PageQuery pageQuery = new PageQuery(page, size, sortBy, sortDirection, paged);
    Page<FieldOfLaw> result = lookupTablesPort.findFieldsOfLaw(
      new FieldOfLawQuery(
        StringUtils.trimToNull(identifier),
        StringUtils.trimToNull(text),
        StringUtils.trimToNull(norm),
        pageQuery
      )
    );
    return new FieldOfLawQueryResponse(result.getContent(), result);
  }
}
