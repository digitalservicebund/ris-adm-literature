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
 * @param normgeberList      The normgeber list ('Juristische Person' or 'Organ')
 * @param fundstellen         The fundstellen.
 * @param aktenzeichenList       The aktenzeichen list.
 */
public record AdmAktivzitierungOverviewElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String inkrafttretedatum,
  String langueberschrift,
  String dokumenttyp,
  List<String> normgeberList,
  List<String> fundstellen,
  List<String> aktenzeichenList
) {}
