package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.UUID;

/**
 * Fundstelle business object
 *
 * @param id          The uuid of the Fundstelle
 * @param zitatstelle The Zitatstelle of the Fundstelle
 * @param periodikum  The Periodikum of this Fundstelle
 */
public record Fundstelle(UUID id, String zitatstelle, Periodikum periodikum) {}
