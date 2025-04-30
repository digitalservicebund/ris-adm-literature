package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import java.util.UUID;

/**
 * Norm abbreviation record.
 *
 * @param id The id
 * @param abbreviation The abbreviation
 * @param officialLongTitle The official long title
 */
public record NormAbbreviation(UUID id, String abbreviation, String officialLongTitle) {}
