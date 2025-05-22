package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.Institution;
import java.util.List;

/**
 * Response with institutions and pagination information
 *
 * @param institutions List of institutions
 * @param page Pagination data
 */
public record InstitutionResponse(List<Institution> institutions, PageResponse page) {}
