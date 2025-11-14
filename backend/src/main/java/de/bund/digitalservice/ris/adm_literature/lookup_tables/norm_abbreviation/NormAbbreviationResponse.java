package de.bund.digitalservice.ris.adm_literature.lookup_tables.norm_abbreviation;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
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
