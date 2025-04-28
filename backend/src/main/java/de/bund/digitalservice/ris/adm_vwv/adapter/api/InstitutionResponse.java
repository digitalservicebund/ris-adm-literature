package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.Institution;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Response with Institutions and pagination information
 *
 * @param institutions List of institutions
 * @param paginatedInstitutions List of paginated institutions
 */
public record InstitutionResponse(
  List<Institution> institutions,
  Page<Institution> paginatedInstitutions
) {}
