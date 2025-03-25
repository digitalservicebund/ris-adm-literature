package de.bund.digitalservice.ris.adm_vwv.application;

import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * DocumentUnit
 *
 * @param documentNumber The publicly known ID of the document
 * @param id The internal (database) id of the document
 * @param json The JSON containing the documentation unit (as required by the frontend's pinia store)
 */
public record DocumentationUnit(
  @Nonnull String documentNumber,
  @Nonnull UUID id,
  @JsonRawValue String json
) {}
