package de.bund.digitalservice.ris.adm_vwv.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import javax.annotation.Nonnull;

public interface LookupTablesPort {
  Page<DocumentType> findBySearchQuery(@Nonnull DocumentTypeQuery query);
}
