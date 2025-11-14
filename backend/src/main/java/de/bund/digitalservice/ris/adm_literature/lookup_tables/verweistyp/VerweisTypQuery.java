package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;

/**
 * The query business object used for looking up verweis typen
 *
 * @param searchTerm String to search for in verweis typen
 * @param queryOptions Details on pagination and sorting
 */
public record VerweisTypQuery(String searchTerm, @Nonnull QueryOptions queryOptions) {}
