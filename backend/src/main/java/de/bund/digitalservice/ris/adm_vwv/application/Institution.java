package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.Set;

/**
 * Institution business object
 *
 * @param name The name of the institution
 * @param officialName The full / official name of the institution can be NULL
 * @param type Either "jurpn" for Juristische Personen and "organ" for Official Institutions
 */
public record Institution(
  @Nonnull String name,
  String officialName,
  String type,
  Set<Region> regions
) {}
