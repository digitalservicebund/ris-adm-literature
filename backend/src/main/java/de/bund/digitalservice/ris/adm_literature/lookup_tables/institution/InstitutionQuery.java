package de.bund.digitalservice.ris.adm_literature.lookup_tables.institution;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;

/**
 * The query business object used for looking up institutions
 *
 * @param searchTerm String to search for in institutions
 * @param queryOptions Details on pagination and sorting
 */
public record InstitutionQuery(String searchTerm, @Nonnull QueryOptions queryOptions) {}
