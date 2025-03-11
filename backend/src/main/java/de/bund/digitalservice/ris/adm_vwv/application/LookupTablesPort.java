package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;

public interface LookupTablesPort {
  List<DocumentType> findBySearchQuery(String searchQuery);
}
