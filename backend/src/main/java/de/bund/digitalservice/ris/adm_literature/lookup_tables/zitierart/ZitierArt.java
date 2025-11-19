package de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 'Zitierart' business object.
 *
 * @param id The unique id
 * @param abbreviation The abbreviation
 * @param label the label
 */
public record ZitierArt(UUID id, @NotNull String abbreviation, @NotNull String label) {}
