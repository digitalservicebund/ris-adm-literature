package de.bund.digitalservice.ris.adm_vwv.application;

import lombok.Builder;

/**
 * Norm business record.
 *
 * @param abbreviation Norm abbreviation, e.g. BGB
 * @param singleNormDescription Single norm description, e.g. § 17
 */
@Builder
public record Norm(String abbreviation, String singleNormDescription) {}
