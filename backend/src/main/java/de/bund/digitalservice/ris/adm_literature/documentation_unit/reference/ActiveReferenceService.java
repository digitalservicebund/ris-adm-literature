package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Active reference service.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ActiveReferenceService {

  private final ActiveReferenceRepository activeReferenceRepository;
  private final DocumentReferenceRepository documentReferenceRepository;

  /**
   * Publishes for a given source document the given targets as active references. If the given targets
   * are empty, this method removes all active references for the given source.
   * <p>
   *   <b>IMPORTANT</b>: This method must be called once per documentation unit with all active references given.
   *   On each method call, at first all found active references of the given source are deleted.
   *   Calling this method for the same documentation unit with different targets (for example, one call
   *   for ADM references, second call for caselaw references) results into loose of active references.
   * </p>
   *
   * @param source The source reference item
   * @param targets The list of target reference items
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publish(@NonNull DocumentReference source, @NonNull List<DocumentReference> targets) {
    final DocumentReferenceEntity sourceDocumentReference = findOrCreateDocumentReference(source);
    // # 1. Delete all active references which are not given anymore
    ActiveReferenceEntity probe = new ActiveReferenceEntity();
    probe.setSource(sourceDocumentReference);
    List<ActiveReferenceEntity> existingActiveReferences = activeReferenceRepository.findAll(
      Example.of(probe)
    );
    // Results into SQL: delete from active_reference where id in ()
    activeReferenceRepository.deleteAllInBatch(
      existingActiveReferences
        .stream()
        .filter(ar -> !targets.contains(new DocumentReference(ar.getTarget())))
        .toList()
    );
    if (targets.isEmpty()) {
      log.info("Deleted active references for source {}.", sourceDocumentReference);
      return;
    }
    // Flush needed before inserting new active references
    activeReferenceRepository.flush();
    // # 2. Create only active references which are new
    List<DocumentReference> existingTargets = existingActiveReferences
      .stream()
      .map(ar -> new DocumentReference(ar.getTarget()))
      .toList();
    List<ActiveReferenceEntity> activeReferenceEntitiesToCreate = targets
      .stream()
      .filter(ar -> !existingTargets.contains(ar))
      .map(target -> {
        // Find or create the target document reference
        DocumentReferenceEntity targetDocumentReference = findOrCreateDocumentReference(target);
        ActiveReferenceEntity activeReferenceEntity = new ActiveReferenceEntity();
        activeReferenceEntity.setSource(sourceDocumentReference);
        activeReferenceEntity.setTarget(targetDocumentReference);
        return activeReferenceEntity;
      })
      .toList();
    // # 3. Saves all newly created active references
    activeReferenceRepository.saveAll(activeReferenceEntitiesToCreate);
    log.info(
      "Saved {} newly created active references for source {}.",
      activeReferenceEntitiesToCreate.size(),
      sourceDocumentReference
    );
  }

  @NotNull
  private DocumentReferenceEntity findOrCreateDocumentReference(
    DocumentReference documentReference
  ) {
    DocumentReferenceEntity probe = new DocumentReferenceEntity();
    probe.setDocumentReference(documentReference);
    return documentReferenceRepository
      .findOne(Example.of(probe))
      .orElseGet(() -> {
        var newDocumentReference = new DocumentReferenceEntity();
        newDocumentReference.setDocumentReference(documentReference);
        return newDocumentReference;
      });
  }
}
