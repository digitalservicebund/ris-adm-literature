package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * DocumentationUnitListElement
 *
 * @param id The uuid of the document unit
 */
public record DocumentationUnitListElement(
  @Nonnull UUID id,
  @Nonnull String dokumentnummer,
  String zitierdatum,
  String langueberschrift,
  List<Fundstelle> fundstellen
) {}
