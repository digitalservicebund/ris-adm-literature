package de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Response with the 'Zitierarten'.
 *
 * @param zitierArten List of 'Zitierarten' (e.g. 'Übernahme')
 * @param page        Pagination data
 */
public record ZitierArtResponse(List<ZitierArt> zitierArten, PageResponse page) {}
