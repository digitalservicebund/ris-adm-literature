package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.data.domain.Page;

public interface LookupTablesPort {
  Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query);
  List<FieldOfLaw> findChildrenOfFieldOfLaw(String identifier);
}
