package de.bund.digitalservice.ris.adm_literature.lookup_tables.court;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Response with courts and pagination information.
 *
 * @param courts List of courts
 * @param page Pagination data
 */
public record CourtResponse(List<Court> courts, PageResponse page) {}
