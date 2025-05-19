package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;
import java.util.UUID;

/**
 * Fundstelle business object
 *
 * @param id          The uuid of the Fundstelle
 * @param zitatstelle The Zitatstelle of the Fundstelle
 * @param periodika   The list of Periodika of this Fundstelle
 */
public record Fundstelle(UUID id, String zitatstelle, List<Periodikum> periodika) {}
