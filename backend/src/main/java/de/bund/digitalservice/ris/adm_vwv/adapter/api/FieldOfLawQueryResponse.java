package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.Sachgebiet;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Field of law query response.
 *
 * @param fieldsOfLaw Fields of law as list
 * @param page        Pagination data
 */
public record FieldOfLawQueryResponse(List<Sachgebiet> fieldsOfLaw, Page<Sachgebiet> page) {}
