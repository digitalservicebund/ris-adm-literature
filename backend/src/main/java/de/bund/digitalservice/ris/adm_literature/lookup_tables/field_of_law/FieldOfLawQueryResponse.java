package de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Field of law query response.
 *
 * @param fieldsOfLaw Fields of law as list
 * @param page Pagination data
 */
public record FieldOfLawQueryResponse(List<FieldOfLaw> fieldsOfLaw, PageResponse page) {}
