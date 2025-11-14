package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * VerweisTyp business object
 *
 * @param id The uuid
 * @param name The name of the verweistyp (e.g. "rechtsgrundlage")
 */
public record VerweisTyp(@Nonnull UUID id, @Nonnull String name) {}
