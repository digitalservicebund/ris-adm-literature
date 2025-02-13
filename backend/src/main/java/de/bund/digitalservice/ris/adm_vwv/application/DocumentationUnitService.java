package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentationUnitService implements DocumentationUnitPort {

  private final DocumentationUnitPersistencePort documentationUnitPersistencePort;

  @Override
  public Optional<DocumentationUnit> findByDocumentNumber(@Nonnull String documentNumber) {
    return documentationUnitPersistencePort.findByDocumentNumber(documentNumber);
  }

  @Override
  public DocumentationUnit create() {
    return documentationUnitPersistencePort.create();
  }

  @Override
  public Optional<DocumentationUnit> update(@Nonnull String documentNumber, @Nonnull String json) {
    return Optional.ofNullable(documentationUnitPersistencePort.update(documentNumber, json));
  }
}
