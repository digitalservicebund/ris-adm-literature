package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;
import javax.annotation.Nonnull;

public interface LookupTablesPersistencePort {
  List<DocumentType> findBySearchQuery(@Nonnull DocumentTypeQuery query);
}
