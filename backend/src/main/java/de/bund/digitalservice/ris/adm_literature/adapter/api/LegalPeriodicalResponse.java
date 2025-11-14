package de.bund.digitalservice.ris.adm_literature.adapter.api;

import de.bund.digitalservice.ris.adm_literature.application.LegalPeriodical;
import java.util.List;

/**
 * Response with LegalPeriodicals and pagination information
 *
 * @param legalPeriodicals List of legal periodicals
 * @param page Pagination data
 */
public record LegalPeriodicalResponse(List<LegalPeriodical> legalPeriodicals, PageResponse page) {}
