package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;

public interface DocumentationUnitPersistencePort {
  DocumentationUnit create();

  DocumentationUnit update(@Nonnull String documentNumber, @Nonnull String json);
}
