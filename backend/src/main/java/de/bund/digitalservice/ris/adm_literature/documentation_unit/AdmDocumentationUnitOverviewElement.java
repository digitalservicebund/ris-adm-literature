package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Element in the documentation unit overview
 *
 * @param id               The uuid of the documentation unit
 * @param documentNumber   The public id of the documentation unit
 * @param zitierdaten      The date to quote by
 * @param langueberschrift The unabbreviated title of the documentation unit
 * @param fundstellen      The list of Fundstellen
 */
public record AdmDocumentationUnitOverviewElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  List<String> zitierdaten,
  String langueberschrift,
  List<String> fundstellen
) {}
