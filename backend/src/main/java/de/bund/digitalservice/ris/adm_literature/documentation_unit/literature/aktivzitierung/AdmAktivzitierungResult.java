package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Represents a search result for a citation of an administrative regulation
 * (Aktivzitierung Verwaltungsvorschrift).
 *
 * @param id                 The unique identifier of the documentation unit.
 * @param documentNumber     The public document number.
 * @param inkrafttretedatum  The effective date of the document.
 * @param langueberschrift   The long title or heading.
 * @param dokumenttyp        The type of document.
 * @param normgeberList      The normgeber list ('Juristische Person' or 'Organ')
 * @param fundstellen        The fundstellen.
 * @param aktenzeichenList   The aktenzeichen list.
 * @param zitierdaten        The zitierdaten
 */
public record AdmAktivzitierungResult(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String inkrafttretedatum,
  String langueberschrift,
  String dokumenttyp,
  List<String> normgeberList,
  List<String> fundstellen,
  List<String> aktenzeichenList,
  List<String> zitierdaten
) {}
