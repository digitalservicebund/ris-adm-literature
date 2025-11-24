package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.norm_abbreviation.NormAbbreviation;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp.VerweisTyp;
import java.util.List;

/**
 * Active reference record.
 *
 * @param referenceDocumentType The reference document type (norm or administrative regulation)
 * @param verweisTyp The 'Verweistyp'
 * @param normAbbreviation The norm abbreviation
 * @param normAbbreviationRawValue Raw value of norm abbreviation
 * @param singleNorms List of single norm entries
 */
public record ActiveReference(
  String referenceDocumentType,
  VerweisTyp verweisTyp,
  NormAbbreviation normAbbreviation,
  String normAbbreviationRawValue,
  List<SingleNorm> singleNorms
) {}
