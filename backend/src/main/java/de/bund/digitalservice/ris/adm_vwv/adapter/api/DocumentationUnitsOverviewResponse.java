package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitOverviewElement;
import org.springframework.data.domain.Page;

/**
 * Response with document unit list elements and pagination information
 *
 * @param paginatedDocumentationUnitsOverview Paginated document units
 */
public record DocumentationUnitsOverviewResponse(
  Page<DocumentationUnitOverviewElement> paginatedDocumentationUnitsOverview
) {}
