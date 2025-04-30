package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import de.bund.digitalservice.ris.adm_vwv.application.LegalPeriodical;
import java.util.UUID;

/**
 * A reference to a legal periodical (Fundstelle).
 *
 * @param id The id of the reference.
 * @param citation The citation example (Zitierung)
 * @param legalPeriodical The periodical, where reference was found.
 * @param legalPeriodicalRawValue Abbreviation of the linked legal periodical
 */
public record Reference(
  UUID id,
  String citation,
  LegalPeriodical legalPeriodical,
  String legalPeriodicalRawValue
) {}
