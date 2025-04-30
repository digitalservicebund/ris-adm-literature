package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * Region business object
 *
 * @param id The uuid
 * @param code The unique code of the region (e.g. "HH")
 * @param longText The full / official name of the region (e.g. "Hamburg") can be {@code null}
 */
public record Region(@Nonnull UUID id, @Nonnull String code, String longText) {}
