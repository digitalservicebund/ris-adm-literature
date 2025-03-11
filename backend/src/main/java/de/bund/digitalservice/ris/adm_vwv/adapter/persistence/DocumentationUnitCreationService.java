package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DocumentationUnitCreationService {

  private final DocumentationUnitRepository documentationUnitRepository;
  private final DocumentNumberRepository documentNumberRepository;

  @Transactional
  public DocumentationUnit create() {
    // 1. Create or update the document number for this year
    Year thisYear = Year.now();
    DocumentNumberEntity documentNumberEntity = documentNumberRepository
      .findByYear(thisYear)
      .orElseGet(() -> {
        DocumentNumberEntity newDocumentNumberEntity = new DocumentNumberEntity();
        newDocumentNumberEntity.setYear(thisYear);
        return newDocumentNumberEntity;
      });
    DocumentNumber documentNumber = new DocumentNumber(thisYear, documentNumberEntity.getLatest());
    String newDocumentNumber = documentNumber.create();
    documentNumberEntity.setLatest(newDocumentNumber);
    documentNumberRepository.save(documentNumberEntity);
    // 2. Create the documentation unit with the created document number
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber(newDocumentNumber);
    DocumentationUnitEntity saved = documentationUnitRepository.save(documentationUnitEntity);
    return new DocumentationUnit(saved.getDocumentNumber(), saved.getId(), null);
  }
}
