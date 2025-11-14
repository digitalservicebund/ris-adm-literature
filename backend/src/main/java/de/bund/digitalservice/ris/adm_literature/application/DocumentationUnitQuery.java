package de.bund.digitalservice.ris.adm_literature.application;

import jakarta.annotation.Nonnull;

/**
 * Field of law query.
 *
 * @param documentNumber DocumentNumber to search for
 * @param langueberschrift Langueberschrift to search for
 * @param fundstellen Fundstellen to search for
 * @param zitierdaten Zitierdaten to search for
 * @param queryOptions Page query options
 */
public record DocumentationUnitQuery(
  String documentNumber,
  String langueberschrift,
  String fundstellen,
  String zitierdaten,
  @Nonnull QueryOptions queryOptions
) {}
