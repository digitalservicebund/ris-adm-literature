package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
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

  private static final Set<String> INDEX_ALIASES = Set.of(
    "veroeffentlichungsjahr",
    "dokumenttypen",
    "buchTitel",
    "verfasser"
  );

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
   * Returns document units overview
   *
   * @return Document unit (should be multiple in the future)
   */
  @GetMapping("api/literature/documentation-units")
  public ResponseEntity<DocumentationUnitsOverviewResponse> find(
    @RequestParam(required = false) String documentNumber,
    @RequestParam(required = false) String veroeffentlichungsjahr,
    @RequestParam(required = false) List<DocumentType> dokumenttypen,
    @RequestParam(required = false) String buchTitel,
    @RequestParam(required = false) List<String> verfasser,
    @RequestParam(defaultValue = "0") int pageNumber,
    @RequestParam(defaultValue = "15") int pageSize,
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

    var paginatedDocumentationUnits =
      documentationUnitService.findLiteratureDocumentationUnitOverviewElements(
        new LiteratureDocumentationUnitQuery(
          StringUtils.trimToNull(documentNumber),
          StringUtils.trimToNull(veroeffentlichungsjahr),
          dokumenttypen,
          StringUtils.trimToNull(buchTitel),
          verfasser,
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
