package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.UliDocumentationUnitContent;
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
import tools.jackson.databind.JsonNode;

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
   * Returns a document unit (should be multiple in the future)
   *
   * @return Document unit (should be multiple in the future)
   */
  @GetMapping("api/literature/documentation-units")
  public ResponseEntity<String> find() {
    String json =
      """
      {
        "documentationUnitsOverview": [
          {
            "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            "documentNumber": "VALID123456789",
            "veroeffentlichungsjahr": "1999-2022",
            "dokumenttypen": [
              {
                "uuid": "11185f64-5717-4562-b3fc-2c963f66afa6",
                "abbreviation": "DokAbbrv",
                "name": "Doktyp 1"
              }
            ],
            "hauptsachtitel": "Dies ist der Hauptsachtitel",
            "dokumentarischerTitel": "Dies ist der dokumentarische Titel",
            "verfasser": [
              "Name 1",
              "Name 2"
            ]
          },
          {
            "id": "33385f64-5717-4562-b3fc-2c963f66afa6",
            "documentNumber": "VALID987654321",
            "veroeffentlichungsjahr": "2025",
            "dokumenttypen": [
              {
                "uuid": "44485f64-5717-4562-b3fc-2c963f66afa6",
                "abbreviation": "DokAbbrv 2",
                "name": "Doktyp 2"
              }
            ],
            "hauptsachtitel": "Dies ist der 2. Hauptsachtitel",
            "dokumentarischerTitel": "Dies ist der 2. dokumentarische Titel",
            "verfasser": [
              "Name 3",
              "Name 4"
            ]
          }
        ],
        "page": {
          "size": 15,
          "number": 0,
          "numberOfElements": 2,
          "totalElements": 2,
          "first": true,
          "last": true,
          "empty": false
        }
      }
      """;
    return ResponseEntity.ok(json);
  }

  /**
   * Creates a new ULI documentation unit with a new document number in database
   * and
   * returns it.
   *
   * @return Created documentation unit
   */
  @PostMapping("api/literature/uli/documentation-units")
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentationUnit createUli() {
    return documentationUnitService.create(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
  }

  /**
   * Creates a new SLI documentation unit with a new document number in database
   * and
   * returns it.
   *
   * @return Created documentation unit
   */
  @PostMapping("api/literature/sli/documentation-units")
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentationUnit createSli() {
    return documentationUnitService.create(DocumentCategory.LITERATUR_SELBSTAENDIG);
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
   * @param documentNumber           The document number of the document to
   *                                 publish
   * @param documentationUnitContent The documentation unit content to publish
   * @return The published documentation unit or
   *         <br>
   *         - HTTP 400 if input not valid
   *         <br>
   *         - HTTP 404 if not found
   *         <br>
   *         - HTTP 503 if the external publishing service is unavailable
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
  @PutMapping(
    {
      "api/literature/uli/documentation-units/{documentNumber}/publish",
      "api/literature/documentation-units/{documentNumber}/publish",
    }
  )
  public ResponseEntity<DocumentationUnit> publishUli(
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

  /**
   * Publishes the documentation unit with the given document number and content.
   *
   * @param documentNumber           The document number of the document to
   *                                 publish
   * @param documentationUnitContent The documentation unit content to publish
   * @return The published documentation unit or
   *         <br>
   *         - HTTP 400 if input not valid
   *         <br>
   *         - HTTP 404 if not found
   *         <br>
   *         - HTTP 503 if the external publishing service is unavailable
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
  @PutMapping("api/literature/sli/documentation-units/{documentNumber}/publish")
  public ResponseEntity<DocumentationUnit> publishSli(
    @PathVariable String documentNumber,
    @RequestBody @Valid SliDocumentationUnitContent documentationUnitContent
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
