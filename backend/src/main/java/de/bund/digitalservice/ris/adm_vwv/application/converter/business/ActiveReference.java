package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import java.util.List;

/**
 * Active reference record.
 *
 * @param referenceDocumentType The reference document type (norm or administrative regulation)
 * @param referenceType The reference type
 * @param normAbbreviation The norm abbreviation
 * @param normAbbreviationRawValue Raw value of norm abbreviation
 * @param singleNorms List of single norm entries
 */
public record ActiveReference(
  String referenceDocumentType,
  String referenceType,
  NormAbbreviation normAbbreviation,
  String normAbbreviationRawValue,
  List<SingleNorm> singleNorms
) {}
