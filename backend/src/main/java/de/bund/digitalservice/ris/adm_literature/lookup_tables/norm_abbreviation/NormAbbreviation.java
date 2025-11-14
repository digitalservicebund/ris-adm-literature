package de.bund.digitalservice.ris.adm_literature.lookup_tables.norm_abbreviation;

import java.util.UUID;

/**
 * Norm abbreviation record.
 *
 * @param id The id
 * @param abbreviation The abbreviation
 * @param officialLongTitle The official long title
 */
public record NormAbbreviation(UUID id, String abbreviation, String officialLongTitle) {}
