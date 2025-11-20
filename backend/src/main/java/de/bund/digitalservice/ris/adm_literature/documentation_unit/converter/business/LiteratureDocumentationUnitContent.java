package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

/**
 * Shared interface for ULI and SLI documentation units.
 * Defines common fields and shared validation logic.
 */
public interface LiteratureDocumentationUnitContent extends IDocumentationContent {
  UUID id();

  @NotBlank
  String documentNumber();

  @NotBlank
  String veroeffentlichungsjahr();

  List<DocumentType> dokumenttypen();
  String hauptsachtitel();
  String hauptsachtitelZusatz();
  String dokumentarischerTitel();
  String note();

  @AssertTrue(message = "Either hauptsachtitel or dokumentarischerTitel must be provided")
  @JsonIgnore
  @SuppressWarnings("unused")
  default boolean isTitlePresent() {
    return (
      (hauptsachtitel() != null && !hauptsachtitel().isBlank()) ||
      (dokumentarischerTitel() != null && !dokumentarischerTitel().isBlank())
    );
  }
}
