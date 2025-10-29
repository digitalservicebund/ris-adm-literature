package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
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
  // 1. Create or update the document number for this year and docOffice documentCategory
  public DocumentationUnit create(DocumentationOffice office, DocumentCategory documentCategory) {
    Year thisYear = Year.now();
    String prefix = office.prefix + documentCategory.getPrefix();
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
    documentationUnitEntity.setDocumentationUnitType(documentCategory);
    documentationUnitEntity.setDocumentationOffice(office);
    DocumentationUnitEntity saved = documentationUnitRepository.save(documentationUnitEntity);
    return new DocumentationUnit(saved.getDocumentNumber(), saved.getId(), null);
  }
}
