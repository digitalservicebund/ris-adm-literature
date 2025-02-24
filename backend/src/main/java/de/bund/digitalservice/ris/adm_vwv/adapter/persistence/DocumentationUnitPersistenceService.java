package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitPersistencePort;
import jakarta.annotation.Nonnull;
import java.time.Year;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentationUnitPersistenceService implements DocumentationUnitPersistencePort {

  private final DocumentationUnitRepository documentationUnitRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber) {
    return documentationUnitRepository
      .findByDocumentNumber(documentNumber)
      .map(documentationUnitEntity ->
        new DocumentationUnit(
          documentNumber,
          documentationUnitEntity.getId(),
          documentationUnitEntity.getJson()
        )
      );
  }

  @Override
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public DocumentationUnit create() {
    Year thisYear = Year.now();
    String lastDocumentNumber = documentationUnitRepository
      .findTopByYearOrderByDocumentNumberDesc(thisYear)
      .map(DocumentationUnitEntity::getDocumentNumber)
      .orElse(null);
    DocumentNumber documentNumber = new DocumentNumber(thisYear, lastDocumentNumber);
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber(documentNumber.create());
    documentationUnitEntity.setYear(thisYear);
    DocumentationUnitEntity saved = documentationUnitRepository.save(documentationUnitEntity);
    log.info("New documentation unit created with document number: {}", saved.getDocumentNumber());
    return new DocumentationUnit(saved.getDocumentNumber(), saved.getId(), null);
  }

  @Override
  @Transactional
  public DocumentationUnit update(@Nonnull String documentNumber, @Nonnull String json) {
    return documentationUnitRepository
      .findByDocumentNumber(documentNumber)
      .map(documentationUnitEntity -> {
        documentationUnitEntity.setJson(json);
        return new DocumentationUnit(documentNumber, documentationUnitEntity.getId(), json);
      })
      .orElse(null);
  }
}
