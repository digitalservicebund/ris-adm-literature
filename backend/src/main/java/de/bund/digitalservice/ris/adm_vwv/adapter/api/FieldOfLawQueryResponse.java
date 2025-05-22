package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import java.util.List;

/**
 * Field of law query response.
 *
 * @param fieldsOfLaw Fields of law as list
 * @param page Pagination data
 */
public record FieldOfLawQueryResponse(List<FieldOfLaw> fieldsOfLaw, PageResponse page) {}
