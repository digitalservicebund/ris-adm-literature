package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.UUID;

/**
 * 'Zitierart' business object.
 *
 * @param id The unique id
 * @param abbreviation The abbreviation
 * @param label the label
 */
public record ZitierArt(UUID id, String abbreviation, String label) {}
