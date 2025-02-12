package de.bund.digitalservice.ris.adm_vwv.application;

import com.fasterxml.jackson.annotation.JsonRawValue;
import java.util.UUID;

/** Documentation unit. */
public record DocumentationUnit(String documentNumber, UUID id, @JsonRawValue String json) {}
