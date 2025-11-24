package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * VerweisTyp business object
 *
 * @param id The uuid
 * @param name The name of the 'Verweistyp' (e.g. "Rechtsgrundlage")
 * @param typNummer The 'Typnummer' (e.g. "82")
 */
public record VerweisTyp(@Nonnull UUID id, @Nonnull String name, @Nonnull String typNummer) {}
