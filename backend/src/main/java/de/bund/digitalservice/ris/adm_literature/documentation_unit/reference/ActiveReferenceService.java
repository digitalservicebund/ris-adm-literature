package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Active reference service.
 */
@RequiredArgsConstructor
@Service
public class ActiveReferenceService {

  private final ActiveReferenceRepository activeReferenceRepository;

  /**
   * Publishes for a given source document the given targets as active references.
   *
   * @param source The source reference item
   * @param targets The list of target reference items (must not be empty)
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publish(@NonNull DocumentReference source, @NonNull List<DocumentReference> targets) {
    if (targets.isEmpty()) {
      throw new IllegalArgumentException("Targets must not be empty.");
    }
    DocumentReferenceEntity sourceDocumentReference = new DocumentReferenceEntity();
    sourceDocumentReference.setDocumentReference(source);
    List<ActiveReferenceEntity> activeReferenceEntities = targets
      .stream()
      .map(dr -> {
        DocumentReferenceEntity targetDocumentReference = new DocumentReferenceEntity();
        targetDocumentReference.setDocumentReference(dr);
        ActiveReferenceEntity activeReferenceEntity = new ActiveReferenceEntity();
        activeReferenceEntity.setSource(sourceDocumentReference);
        activeReferenceEntity.setTarget(targetDocumentReference);
        return activeReferenceEntity;
      })
      .toList();
    activeReferenceRepository.saveAll(activeReferenceEntities);
  }
}
