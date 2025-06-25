package de.bund.digitalservice.ris.adm_vwv.application;

/**
 * Field of law query.
 *
 * @param documentNumber DocumentNumber to search for
 * @param langueberschrift Langueberschrift to search for
 * @param fundstellen Fundstellen to search for
 * @param zitierdaten Zitierdaten to search for
 */
public record DocumentationUnitQuery(
  String documentNumber,
  String langueberschrift,
  String fundstellen,
  String zitierdaten
) {}
