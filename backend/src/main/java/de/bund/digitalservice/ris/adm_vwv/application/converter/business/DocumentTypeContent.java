package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import jakarta.annotation.Nonnull;

/**
 * Document type content business object.
 *
 * @param abbreviation The abbreviated name of the document type (e.g. "VV")
 * @param label The full / official of the document type (e.g. "Verwaltungsvereinbarung")
 */
public record DocumentTypeContent(@Nonnull String abbreviation, @Nonnull String label) {
  // TODO: Use DocumentType instead of this
}
