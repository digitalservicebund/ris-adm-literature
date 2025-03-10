package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DokumentTypController {
    @GetMapping("api/wertetabellen/dokument-typ")
    public ResponseEntity<String> getDocumentTypes() {
        return ResponseEntity.ok(null);
    }
}
