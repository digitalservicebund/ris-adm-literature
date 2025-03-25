package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;

/**
 * DocumentType business object
 *
 * @param abbreviation The abbreviated name of the document type (e.g. "VV")
 * @param name The full / official of the document type
 */
public record DocumentType(@Nonnull String abbreviation, @Nonnull String name) {}
