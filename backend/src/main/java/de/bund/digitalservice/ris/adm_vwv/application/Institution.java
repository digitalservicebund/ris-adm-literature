package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.Set;

/**
 * Institution business object
 *
 * @param name The name of the institution
 * @param officialName The full / official name of the institution can be NULL
 * @param type The type of institution
 */
public record Institution(
  @Nonnull String name,
  String officialName,
  InstitutionType type,
  Set<Region> regions
) {}
