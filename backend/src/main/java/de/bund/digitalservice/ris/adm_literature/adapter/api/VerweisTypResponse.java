package de.bund.digitalservice.ris.adm_literature.adapter.api;

import de.bund.digitalservice.ris.adm_literature.application.converter.business.VerweisTyp;
import java.util.List;

/**
 * Response with the verweisTypen.
 *
 * @param verweisTypen List of verweisTypen (e.g. anwendung)
 * @param page Pagination data
 */
public record VerweisTypResponse(List<VerweisTyp> verweisTypen, PageResponse page) {}
