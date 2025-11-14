package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Response with document unit list elements and pagination information
 *
 * @param documentationUnitsOverview          List of document units' data
 * @param page Pagination data
 */
public record DocumentationUnitsOverviewResponse(
  List<DocumentationUnitOverviewElement> documentationUnitsOverview,
  PageResponse page
) {}
