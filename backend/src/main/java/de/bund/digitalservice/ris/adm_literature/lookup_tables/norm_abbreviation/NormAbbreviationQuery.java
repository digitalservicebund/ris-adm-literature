package de.bund.digitalservice.ris.adm_literature.lookup_tables.norm_abbreviation;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;

/**
 * The query business object used for looking up norm abbreviations
 *
 * @param searchTerm String to search for in norm abbreviations
 * @param queryOptions Details on pagination and sorting
 */
public record NormAbbreviationQuery(String searchTerm, @Nonnull QueryOptions queryOptions) {}
