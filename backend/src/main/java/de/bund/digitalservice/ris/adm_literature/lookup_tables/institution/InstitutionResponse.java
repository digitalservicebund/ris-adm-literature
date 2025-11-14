package de.bund.digitalservice.ris.adm_literature.lookup_tables.institution;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Response with institutions and pagination information
 *
 * @param institutions List of institutions
 * @param page Pagination data
 */
public record InstitutionResponse(List<Institution> institutions, PageResponse page) {}
