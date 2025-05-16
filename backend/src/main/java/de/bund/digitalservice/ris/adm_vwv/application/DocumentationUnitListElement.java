package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * DocumentationUnitListElement
 *
 * @param id The uuid of the document unit
 */
public record DocumentationUnitListElement(
  // @Nonnull UUID id,
  // @Nonnull String name,
  // String officialName,
  // InstitutionType type,
  // List<Region> regions

  @Nonnull UUID id
  // String dokumentNummer,
  // String zitierdatum,
  // String langueberschrift,
  // List<Fundstelle> fundstellen
) {}
