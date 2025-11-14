package de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;

/**
 * The query business object used for looking up 'Zitierarten'.
 *
 * @param searchTerm String to search for in 'Zitierarten'
 * @param queryOptions Details on pagination and sorting
 */
public record ZitierArtQuery(String searchTerm, @Nonnull QueryOptions queryOptions) {}
