package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import java.util.UUID;

/**
 * Single norm record.
 *
 * @param id The id
 * @param singleNorm Single norm string (e.g. "§ 1")
 * @param dateOfVersion The date of version (dd.MM.yyyy)
 * @param dateOfRelevance The date of relevance (yyyy)
 */
public record SingleNorm(
  UUID id,
  String singleNorm,
  String dateOfVersion,
  String dateOfRelevance
) {}
