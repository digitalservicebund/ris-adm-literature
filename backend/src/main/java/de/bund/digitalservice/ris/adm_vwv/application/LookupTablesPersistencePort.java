package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.List;

public interface LookupTablesPersistencePort {
  List<DocumentType> findBySearchQuery(@Nonnull String searchQuery);
}
