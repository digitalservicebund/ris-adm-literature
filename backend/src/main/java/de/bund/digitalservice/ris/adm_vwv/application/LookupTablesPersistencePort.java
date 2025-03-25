package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Page;

/**
 * Port for lookup table access
 */
public interface LookupTablesPersistencePort {
  Page<DocumentType> findBySearchQuery(@Nonnull DocumentTypeQuery query);
}
