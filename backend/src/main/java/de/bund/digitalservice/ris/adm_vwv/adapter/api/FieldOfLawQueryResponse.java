package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import org.springframework.data.domain.Page;

/**
 * Field of law query response.
 *
 * @param page Pagination data
 */
public record FieldOfLawQueryResponse(Page<FieldOfLaw> page) {}
