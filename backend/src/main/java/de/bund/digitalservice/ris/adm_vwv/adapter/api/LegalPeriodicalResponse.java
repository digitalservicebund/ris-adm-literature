package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.LegalPeriodical;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Response with LegalPeriodicals and pagination information
 *
 * @param legalPeriodicals List of legal periodicals
 * @param paginatedLegalPeriodicals List of paginated legal periodicals
 */
public record LegalPeriodicalResponse(
  List<LegalPeriodical> legalPeriodicals,
  Page<LegalPeriodical> paginatedLegalPeriodicals
) {}
