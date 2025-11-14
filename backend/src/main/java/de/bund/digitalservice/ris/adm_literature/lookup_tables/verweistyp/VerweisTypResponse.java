package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Response with the verweisTypen.
 *
 * @param verweisTypen List of verweisTypen (e.g. anwendung)
 * @param page Pagination data
 */
public record VerweisTypResponse(List<VerweisTyp> verweisTypen, PageResponse page) {}
