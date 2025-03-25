package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Page;

/**
 * Output persistence port for lookup tables.
 */
public interface LookupTablesPersistencePort {
  Page<DocumentType> findBySearchQuery(@Nonnull DocumentTypeQuery query);
}
