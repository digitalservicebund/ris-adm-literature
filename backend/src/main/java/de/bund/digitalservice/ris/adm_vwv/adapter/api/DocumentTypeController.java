package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentType;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentTypeController {

  @GetMapping("api/lookup-tables/document-types")
  public ResponseEntity<List<DocumentType>> getDocumentTypes() {
    var dokumentTypList = List.of(
      new DocumentType("VE", "Verwaltungsvereinbarung"),
      new DocumentType("VR", "Verwaltungsregelung")
    );
    return ResponseEntity.ok(dokumentTypList);
  }
}
