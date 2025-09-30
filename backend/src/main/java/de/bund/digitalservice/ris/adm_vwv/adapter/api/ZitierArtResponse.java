package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.ZitierArt;
import java.util.List;

/**
 * Response with the 'Zitierarten'.
 *
 * @param zitierArten List of 'Zitierarten' (e.g. 'Übernahme')
 * @param page        Pagination data
 */
public record ZitierArtResponse(List<ZitierArt> zitierArten, PageResponse page) {}
