package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.annotation.Nonnull;

public record DocumentType(@Nonnull String abbreviation, @Nonnull String name) {}
