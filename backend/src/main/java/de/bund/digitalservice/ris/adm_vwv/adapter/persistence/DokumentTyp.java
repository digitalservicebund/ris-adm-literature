package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.annotation.Nonnull;

public record DokumentTyp(
  @Nonnull String label,
  @Nonnull String value
) {}

