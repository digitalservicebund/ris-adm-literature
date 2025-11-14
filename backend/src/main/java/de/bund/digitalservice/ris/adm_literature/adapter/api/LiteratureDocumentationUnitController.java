package de.bund.digitalservice.ris.adm_literature.adapter.api;

import com.fasterxml.jackson.databind.JsonNode;
import de.bund.digitalservice.ris.adm_literature.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.application.DocumentationUnitService;
import de.bund.digitalservice.ris.adm_literature.application.converter.business.UliDocumentationUnitContent;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for CRUD on literature units.
 */
@RestController
@RequiredArgsConstructor
public class LiteratureDocumentationUnitController {

  private final DocumentationUnitService documentationUnitService;

  /**
   * Returns a single documentation unit by its document number
   *
   * @param documentNumber The document number of the document unit to be returned
   * @return The document unit or HTTP 404 if not found
   */
  @GetMapping("api/literature/documentation-units/{documentNumber}")
  public ResponseEntity<DocumentationUnit> find(@PathVariable String documentNumber) {
    return documentationUnitService
      .findByDocumentNumber(documentNumber)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Creates a new ULI documentation unit with a new document number in database and
   * returns it.
   *
   * @return Created documentation unit
   */
  @PostMapping("api/literature/uli/documentation-units")
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentationUnit createUli() {
    return documentationUnitService.create(DocumentCategory.LITERATUR_UNSELBSTSTAENDIG);
  }

  /**
   * Creates a new SLI documentation unit with a new document number in database and
   * returns it.
   *
   * @return Created documentation unit
   */
  @PostMapping("api/literature/sli/documentation-units")
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentationUnit createSli() {
    return documentationUnitService.create(DocumentCategory.LITERATUR_SELBSTSTAENDIG);
  }

  /**
   * Updates a documentation unit
   *
   * @param documentNumber    The document number of the document to update
   * @param documentationUnit The JSON of the documentation unit to update
   * @return The updated documentation unit or HTTP 404 if not found
   */
  @PutMapping("api/literature/documentation-units/{documentNumber}")
  public ResponseEntity<DocumentationUnit> update(
    @PathVariable String documentNumber,
    @RequestBody JsonNode documentationUnit
  ) {
    return documentationUnitService
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
   *         <br>- HTTP 400 if input not valid
   *         <br>- HTTP 404 if not found
   *         <br>- HTTP 503 if the external publishing service is unavailable
   */
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully published the documentation unit",
        content = {
          @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = DocumentationUnit.class)
          ),
        }
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input, validation failed for the request body",
        content = @Content
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Documentation unit with the given number was not found",
        content = @Content
      ),
      @ApiResponse(
        responseCode = "503",
        description = "The external publishing service is unavailable",
        content = @Content
      ),
    }
  )
  @PutMapping("api/literature/documentation-units/{documentNumber}/publish")
  public ResponseEntity<DocumentationUnit> publish(
    @PathVariable String documentNumber,
    @RequestBody @Valid UliDocumentationUnitContent documentationUnitContent
  ) {
    Optional<DocumentationUnit> optionalDocumentationUnit = documentationUnitService.publish(
      documentNumber,
      documentationUnitContent
    );
    return optionalDocumentationUnit
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }
}
