package de.bund.digitalservice.ris.adm_vwv.application;

import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * DocumentationUnit business object
 *
 * @param documentNumber The publicly known number of the document
 * @param id The internal (database) id of the document
 * @param json The JSON containing the documentation unit (persisting the frontend's pinia store state)
 */
public record DocumentationUnit(
  @Nonnull String documentNumber,
  @Nonnull UUID id,
  @JsonRawValue String json
) {}
