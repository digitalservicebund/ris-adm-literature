package de.bund.digitalservice.ris.adm_literature.application;

import jakarta.annotation.Nonnull;

/**
 * The query business object used for looking up legal periodicals
 *
 * @param searchTerm String to search for in legal periodicals
 * @param queryOptions Details on pagination and sorting
 */
public record LegalPeriodicalQuery(String searchTerm, @Nonnull QueryOptions queryOptions) {}
