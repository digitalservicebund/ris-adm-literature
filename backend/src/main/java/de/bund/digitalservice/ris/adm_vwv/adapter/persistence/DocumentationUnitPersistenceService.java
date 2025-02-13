package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitPersistencePort;
import jakarta.annotation.Nonnull;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentationUnitPersistenceService implements DocumentationUnitPersistencePort {

  private final DocumentationUnitRepository documentationUnitRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber) {
    return documentationUnitRepository
      .findByDocumentNumber(documentNumber)
      .map(documentationUnitEntity ->
        new DocumentationUnit(documentNumber, documentationUnitEntity.getId(), documentationUnitEntity.getJson()));
  }

  @Override
  @Transactional
  public DocumentationUnit create() {
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber(
      "KSNR" +
      LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
      new SecureRandom().nextInt(1000)
    );
    DocumentationUnitEntity saved = documentationUnitRepository.save(documentationUnitEntity);
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
