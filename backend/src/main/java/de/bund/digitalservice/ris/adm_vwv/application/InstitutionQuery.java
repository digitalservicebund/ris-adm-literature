package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;

/**
 * The query business object used for looking up institutions
 *
 * @param searchTerm String to search for in institutions
 * @param queryOptions Details on pagination and sorting
 */
public record InstitutionQuery(String searchTerm, @Nonnull QueryOptions queryOptions) {}
