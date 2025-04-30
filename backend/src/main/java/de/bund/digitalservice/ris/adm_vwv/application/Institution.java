package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Institution business object
 *
 * @param id The uuid
 * @param name The name of the institution
 * @param officialName The full / official name of the institution can be {@code null}
 * @param type The type of institution
 * @param regions Associated regions, can be empty
 */
public record Institution(
  @Nonnull UUID id,
  @Nonnull String name,
  String officialName,
  InstitutionType type,
  List<Region> regions
) {}
