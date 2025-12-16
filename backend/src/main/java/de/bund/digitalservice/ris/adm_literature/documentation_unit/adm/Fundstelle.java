package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical.LegalPeriodical;
import java.util.UUID;

/**
 * A reference to a legal periodical (Fundstelle).
 *
 * @param id                  The id of the reference.
 * @param zitatstelle         The citation
 * @param periodikum          The periodikum, where reference was found.
 *                            If multiple periodika were found with same
 *                            abbreviation then this field is set to null and
 *                            ambiguous periodikum is set instead.
 * @param ambiguousPeriodikum If set, periodikum is not unique
 */
public record Fundstelle(
  UUID id,
  String zitatstelle,
  LegalPeriodical periodikum,
  String ambiguousPeriodikum
) {
  /**
   * Returns a formatted string of this fundstelle containing the legal periodical abbreviation and citation.
   * @return Formatted string
   */
  public String toFormattedString() {
    return (
      (ambiguousPeriodikum != null ? ambiguousPeriodikum : periodikum.abbreviation()) +
      " " +
      zitatstelle
    );
  }
}
