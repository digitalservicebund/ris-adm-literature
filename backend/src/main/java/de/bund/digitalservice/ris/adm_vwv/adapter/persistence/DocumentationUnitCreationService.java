package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentTypeCode;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationOffice;
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
  // 1. Create or update the document number for this year and docOffice type
  public DocumentationUnit create(DocumentationOffice office, DocumentTypeCode type) {
    Year thisYear = Year.now();
    String prefix = office.prefix + type.prefix;
    // 1. Create or update the document number for this year
    DocumentNumberEntity documentNumberEntity = documentNumberRepository
      .findByPrefixAndYear(prefix, thisYear)
      .orElseGet(() -> {
        DocumentNumberEntity newDocumentNumberEntity = new DocumentNumberEntity();
        newDocumentNumberEntity.setYear(thisYear);
        newDocumentNumberEntity.setPrefix(prefix);
        return newDocumentNumberEntity;
      });
    DocumentNumber documentNumber = new DocumentNumber(
      prefix,
      thisYear,
      documentNumberEntity.getLatest()
    );
    String newDocumentNumber = documentNumber.create();
    documentNumberEntity.setLatest(newDocumentNumber);
    documentNumberRepository.save(documentNumberEntity);
    // 2. Create the documentation unit with the created document number
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber(newDocumentNumber);
    documentationUnitEntity.setDocumentationUnitType(type);
    documentationUnitEntity.setDocumentationOffice(office);
    DocumentationUnitEntity saved = documentationUnitRepository.save(documentationUnitEntity);
    return new DocumentationUnit(saved.getDocumentNumber(), saved.getId(), null);
  }
}
