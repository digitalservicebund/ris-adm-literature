package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;

/**
 * Region business object
 *
 * @param code The unique code of the region (e.g. "HH")
 * @param longText The full / official name of the region (e.g. "Hamburg") can be NULL
 */
public record Region(@Nonnull String code, String longText) {}
