package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;

/**
 * DocumentType business object
 *
 * @param abbreviation The abbreviated name of the documen type
 * @param name The full / official of the document type
 */
public record DocumentType(@Nonnull String abbreviation, @Nonnull String name) {}
