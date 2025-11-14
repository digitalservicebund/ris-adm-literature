package de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Response with LegalPeriodicals and pagination information
 *
 * @param legalPeriodicals List of legal periodicals
 * @param page Pagination data
 */
public record LegalPeriodicalResponse(List<LegalPeriodical> legalPeriodicals, PageResponse page) {}
