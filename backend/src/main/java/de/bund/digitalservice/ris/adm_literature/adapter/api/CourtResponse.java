package de.bund.digitalservice.ris.adm_literature.adapter.api;

import de.bund.digitalservice.ris.adm_literature.application.converter.business.Court;
import java.util.List;

/**
 * Response with courts and pagination information.
 *
 * @param courts List of courts
 * @param page Pagination data
 */
public record CourtResponse(List<Court> courts, PageResponse page) {}
