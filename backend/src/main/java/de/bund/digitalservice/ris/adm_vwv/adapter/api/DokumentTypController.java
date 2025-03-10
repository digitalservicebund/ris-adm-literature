package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DokumentTyp;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DokumentTypController {

  @GetMapping("api/wertetabellen/dokument-typ")
  public ResponseEntity<List<DokumentTyp>> getDocumentTypes() {
    var dokumentTypList = List.of(
      new DokumentTyp("VW", "Verwaltungsvorschrift"),
      new DokumentTyp("VR", "Verwaltungsrichtlinie")
    );
    return ResponseEntity.ok(dokumentTypList);
  }
}
