package de.bund.digitalservice.ris.adm_literature.application.converter.business;

import java.util.List;

/**
 * Norm reference record.
 *
 * @param normAbbreviation The norm abbreviation
 * @param normAbbreviationRawValue Raw value of norm abbreviation
 * @param singleNorms List of single norm entries
 */
public record NormReference(
  NormAbbreviation normAbbreviation,
  String normAbbreviationRawValue,
  List<SingleNorm> singleNorms
) {}
