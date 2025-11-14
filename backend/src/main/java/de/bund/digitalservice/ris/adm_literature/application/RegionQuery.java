package de.bund.digitalservice.ris.adm_literature.application;

import jakarta.annotation.Nonnull;

/**
 * The query business object used for looking up regions
 *
 * @param searchTerm String to search for in regions
 * @param queryOptions Details on pagination and sorting
 */
public record RegionQuery(String searchTerm, @Nonnull QueryOptions queryOptions) {}
