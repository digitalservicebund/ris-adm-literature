package de.bund.digitalservice.ris.adm_vwv.application;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;

public interface LookupTablesPersistencePort {
  Page<DocumentType> findBySearchQuery(@Nonnull DocumentTypeQuery query);
}
