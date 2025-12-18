package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Represents an overview element for an administrative citation (Aktivzitierung).
 *
 * @param id                 The unique identifier of the documentation unit.
 * @param documentNumber     The public document number.
 * @param inkrafttretedatum  The effective date of the document.
 * @param langueberschrift   The long title or heading.
 * @param dokumenttyp        The type of document.
 * @param normgeber          The issuing authority.
 * @param periodikum         The periodikum.
 * @param zitatstelle        The zitatstelle.
 * @param aktenzeichen       The aktenzeichen.
 */
public record AdmAktivzitierungOverviewElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String inkrafttretedatum,
  String langueberschrift,
  String dokumenttyp,
  List<String> normgeber,
  List<String> periodikum,
  List<String> zitatstelle,
  List<String> aktenzeichen
) {}
