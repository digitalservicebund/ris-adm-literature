package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.LegalPeriodical;
import java.util.List;

/**
 * Response with LegalPeriodicals and pagination information
 *
 * @param legalPeriodicals List of legal periodicals
 * @param page Pagination data
 */
public record LegalPeriodicalResponse(List<LegalPeriodical> legalPeriodicals, PageResponse page) {}
