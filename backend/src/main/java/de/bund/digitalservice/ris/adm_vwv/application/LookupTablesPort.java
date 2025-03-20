package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LookupTablesPort {
  Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query);
  List<FieldOfLaw> findChildrenOfFieldOfLaw(String identifier);
}
