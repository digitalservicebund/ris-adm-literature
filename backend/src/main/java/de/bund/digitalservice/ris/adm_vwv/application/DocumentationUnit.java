package de.bund.digitalservice.ris.adm_vwv.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * DocumentationUnit business object
 *
 * @param documentNumber The publicly known number of the document
 * @param id The internal (database) id of the document
 * @param json The JSON containing the documentation unit (persisting the frontend's pinia store state),
 *             can be {@code null} for migrated documentation units
 * @param xml The xml, can be {@code null} for new documentation units
 */
public record DocumentationUnit(
  @Nonnull String documentNumber,
  @Nonnull UUID id,
  @JsonRawValue String json,
  @JsonIgnore String xml
) {
  public DocumentationUnit(@Nonnull String documentNumber, @Nonnull UUID id, String json) {
    this(documentNumber, id, json, null);
  }
  public DocumentationUnit(@Nonnull DocumentationUnit documentationUnit, @Nonnull String json) {
    this(documentationUnit.documentNumber, documentationUnit.id, json, null);
  }

  /**
   * Returns {@code true} if this documentation unit has no content, json and xml are set to {@code null}.
   *
   * @return {@code true} if this documentation unit is empty, {@code false} otherwise
   */
  public boolean isEmpty() {
    return json == null && xml == null;
  }
}
