package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;

/**
 * Field of law query.
 *
 * @param identifier Identifier to search for
 * @param text Text to search for
 * @param norm Norm to search for
 * @param queryOptions Page query
 */
public record FieldOfLawQuery(
  String identifier,
  String text,
  String norm,
  @Nonnull QueryOptions queryOptions
) {}
