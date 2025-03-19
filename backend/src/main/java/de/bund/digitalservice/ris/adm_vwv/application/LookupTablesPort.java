package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;
import javax.annotation.Nonnull;

public interface LookupTablesPort {
  List<DocumentType> findBySearchQuery(@Nonnull DocumentTypeQuery query);
}
