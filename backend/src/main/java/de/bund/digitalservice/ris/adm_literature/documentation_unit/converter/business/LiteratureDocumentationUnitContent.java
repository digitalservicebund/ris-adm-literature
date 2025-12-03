package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

/**
 * Shared interface for ULI and SLI documentation units.
 * Defines common fields and shared validation logic.
 */
public interface LiteratureDocumentationUnitContent extends DocumentationUnitContent {
  UUID id();

  @NotBlank
  String documentNumber();

  @NotBlank
  String veroeffentlichungsjahr();

  @NotEmpty
  List<DocumentType> dokumenttypen();

  String hauptsachtitel();

  String hauptsachtitelZusatz();

  String dokumentarischerTitel();

  String note();

  @AssertTrue(message = "Either hauptsachtitel or dokumentarischerTitel must be provided")
  @JsonIgnore
  @SuppressWarnings("unused")
  default boolean isTitelPresent() {
    return (
      StringUtils.isNotBlank(hauptsachtitel()) || StringUtils.isNotBlank(dokumentarischerTitel())
    );
  }

  /**
   * Returns either 'Hauptsachtitel' or 'dokumentarischer Titel' or {@code null} if none of them set.
   * @return Titel if set, {@code null} otherwise
   */
  @JsonIgnore
  default String titel() {
    // Used for indexing it is sufficient to check for null. If both fields are null, titel in index is as well.
    return hauptsachtitel() != null ? hauptsachtitel() : dokumentarischerTitel();
  }
}
