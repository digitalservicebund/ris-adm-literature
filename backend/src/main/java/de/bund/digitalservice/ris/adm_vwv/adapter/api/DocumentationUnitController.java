package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import com.fasterxml.jackson.databind.JsonNode;
import de.bund.digitalservice.ris.adm_vwv.application.CreateDocumentationUnitPort;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DocumentationUnitController {

  private final CreateDocumentationUnitPort createDocumentationUnitPort;

  /**
   * Creates a new documentation unit with a new document number in database and returns it.
   *
   * @return Created documentation unit
   */
  @PostMapping("api/documentation-units")
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentationUnit create() {
    return createDocumentationUnitPort.create();
  }

  @PutMapping("api/documentation-units/{documentNumber}")
  public ResponseEntity<DocumentationUnit> update(@PathVariable String documentNumber, @RequestBody JsonNode documentationUnit) {
    return createDocumentationUnitPort.update(documentNumber, documentationUnit.toString())
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }
}
