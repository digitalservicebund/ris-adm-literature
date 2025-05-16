package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import com.fasterxml.jackson.databind.JsonNode;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for CRUD on documentation units.
 */
@RestController
@RequiredArgsConstructor
public class DocumentationUnitController {

  private final DocumentationUnitPort documentationUnitPort;

  @GetMapping("api/documentation-units")
  public ResponseEntity<List<String>> getAll() {
    return ResponseEntity.ok(List.of("result"));
  }

  /**
   * Returns a single documentation unit by its document number
   *
   * @param documentNumber The document number of the document unit to be returned
   *
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
   *
   * @return the updated documentation unit or HTTP 404 if not found
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
}
