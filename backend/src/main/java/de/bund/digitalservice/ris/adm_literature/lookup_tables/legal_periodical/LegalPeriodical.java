package de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical;

import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * LegalPeriodical business object
 *
 * @param id The uuid
 * @param abbreviation The abbreviated name of the legal periodical (e.g. "AA")
 * @param publicId The public id for referencing, is {@code null} for migrated documentation units
 * @param title The main title (e.g. "Amtlicher Anzeiger")
 * @param subtitle The subtitle (e.g. "Arbeitsrecht optimal gestalten und erfolgreich anwenden")
 * @param citationStyle The template how citation shall be done (e.g. "1969, 138-140; BKK 2007, Sonderbeilage, 1-5")
 */
public record LegalPeriodical(
  @Nonnull UUID id,
  @Nonnull String abbreviation,
  String publicId,
  String title,
  String subtitle,
  String citationStyle
) {}
