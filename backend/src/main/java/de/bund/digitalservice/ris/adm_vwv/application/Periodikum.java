package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.UUID;

/**
 * A legal Periodikum
 *
 * @param id           The id of the Periodikum
 * @param title        The title of the Periodikum
 * @param subtitle     The subtitle of the Periodikum
 * @param abbreviation The abbreviated title of the Periodikum
 */
public record Periodikum(UUID id, String title, String subtitle, String abbreviation) {}
