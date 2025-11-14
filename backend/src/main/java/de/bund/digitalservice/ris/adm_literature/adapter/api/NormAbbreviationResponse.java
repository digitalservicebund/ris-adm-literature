package de.bund.digitalservice.ris.adm_literature.adapter.api;

import de.bund.digitalservice.ris.adm_literature.application.converter.business.NormAbbreviation;
import java.util.List;

/**
 * Response with norm abbreviations and pagination information.
 *
 * @param normAbbreviations List of norm abbreviations
 * @param page Pagination data
 */
public record NormAbbreviationResponse(
  List<NormAbbreviation> normAbbreviations,
  PageResponse page
) {}
