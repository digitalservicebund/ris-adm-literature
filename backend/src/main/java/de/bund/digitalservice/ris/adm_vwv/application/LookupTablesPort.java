package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Page;

/**
 * The port for looking up document types
 */
public interface LookupTablesPort {
  Page<DocumentType> findBySearchQuery(@Nonnull DocumentTypeQuery query);
}
