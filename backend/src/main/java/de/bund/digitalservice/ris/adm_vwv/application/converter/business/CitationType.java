package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import java.util.UUID;

/**
 * Citation type record.
 *
 * @param uuid The uuid
 * @param jurisShortcut Juris shortcut
 * @param label The label
 */
@Deprecated
public record CitationType(UUID uuid, String jurisShortcut, String label) {}
