package de.bund.digitalservice.ris.adm_literature.documentation_unit.references;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReferencesService {

  private final ActiveReferenceRepository activeReferenceRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publish(ReferenceItem source, List<ReferenceItem> targets) {
    DocumentReferenceEntity sourceDocumentReference = new DocumentReferenceEntity();
    sourceDocumentReference.setReference(source);
    List<ActiveReferenceEntity> activeReferenceEntities = targets
      .stream()
      .map(ri -> {
        DocumentReferenceEntity targetDocumentReference = new DocumentReferenceEntity();
        targetDocumentReference.setReference(ri);
        ActiveReferenceEntity activeReferenceEntity = new ActiveReferenceEntity();
        activeReferenceEntity.setSource(sourceDocumentReference);
        activeReferenceEntity.setTarget(targetDocumentReference);
        return activeReferenceEntity;
      })
      .toList();
    activeReferenceRepository.saveAll(activeReferenceEntities);
  }
}
