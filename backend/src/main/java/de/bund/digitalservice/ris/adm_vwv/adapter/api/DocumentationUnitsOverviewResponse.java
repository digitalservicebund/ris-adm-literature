package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitOverviewElement;
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
