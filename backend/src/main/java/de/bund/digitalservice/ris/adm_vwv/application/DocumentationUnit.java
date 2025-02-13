package de.bund.digitalservice.ris.adm_vwv.application;

import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.annotation.Nonnull;
import java.util.UUID;

/** Documentation unit. */
public record DocumentationUnit(
  @Nonnull String documentNumber,
  @Nonnull UUID id,
  @JsonRawValue String json
) {}
