package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;

/**
 * LegalPeriodical business object
 *
 * @param abbreviation The abbreviated name of the legal periodical (e.g. "AA")
 * @param title The main title (e.g. "Amtlicher Anzeiger")
 * @param subtitle The subtitle (e.g. "Arbeitsrecht optimal gestalten und erfolgreich anwenden")
 * @param citation_style The template how citation shall be done (e.g. "1969, 138-140; BKK 2007, Sonderbeilage, 1-5")
 */
public record LegalPeriodical(
  @Nonnull String abbreviation,
  String title,
  String subtitle,
  String citation_style
) {}
