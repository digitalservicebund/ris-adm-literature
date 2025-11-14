package de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law;

import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Field of law response wrapping a list of fields of law.
 *
 * @param fieldsOfLaw Fields of law
 */
public record FieldOfLawResponse(@Nonnull List<FieldOfLaw> fieldsOfLaw) {}
