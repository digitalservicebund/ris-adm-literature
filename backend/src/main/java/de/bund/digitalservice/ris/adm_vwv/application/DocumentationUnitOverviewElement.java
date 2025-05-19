package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Element in the documentation unit overview
 *
 * @param id               The uuid of the documentation unit
 * @param documentNumber   The public id of the documentation unit
 * @param zitierdatum      The date to quote by
 * @param langueberschrift The unabbreviated title of the documentation unit
 * @param fundstellen      The list of Fundstellen
 */
public record DocumentationUnitOverviewElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String zitierdatum,
  String langueberschrift,
  List<Fundstelle> fundstellen
) {}
