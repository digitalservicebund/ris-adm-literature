package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DocumentTypeController {

  private final LookupTablesPort lookupTablesPort;

  @GetMapping("api/lookup-tables/document-types")
  public ResponseEntity<DocumentTypeResponse> getDocumentTypes(@RequestParam(required = false) String searchQuery) {
    return ResponseEntity.ok(new DocumentTypeResponse(lookupTablesPort.findBySearchQuery(searchQuery)));
  }
}
