package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Passive reference service for reading passive references.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PassiveReferenceService {

  private final RefViewActiveReferenceSliAdmRepository refViewActiveReferenceSliAdmRepository;

  /**
   * Returns all passive references for the given target document category.
   *
   * @param documentCategory The target document category
   * @return Passive references
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public List<PassiveReference> findAll(@NonNull DocumentCategory documentCategory) {
    List<PassiveReference> passiveReferences;
    switch (documentCategory) {
      case VERWALTUNGSVORSCHRIFTEN -> passiveReferences = refViewActiveReferenceSliAdmRepository
        .findAll()
        .stream()
        .map(rf ->
          new PassiveReference(
            new DocumentReference(
              rf.getTargetDocumentNumber(),
              DocumentCategory.VERWALTUNGSVORSCHRIFTEN
            ),
            new DocumentReference(
              rf.getSourceDocumentNumber(),
              DocumentCategory.LITERATUR_SELBSTAENDIG
            )
          )
        )
        .toList();
      case LITERATUR_SELBSTAENDIG, LITERATUR_UNSELBSTAENDIG -> {
        passiveReferences = new ArrayList<>();
        log.info("Passive references for literature is not supported.");
      }
      default -> passiveReferences = new ArrayList<>();
    }
    return passiveReferences;
  }

  /**
   * Returns the referenced-by document references (the documents which are citing actively the given document)
   * for the given document number and category.
   *
   * @param documentNumber   The document number of the target
   * @param documentCategory The document category of the target
   * @return Referenced-by list
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public List<DocumentReference> findByDocumentNumber(
    @NonNull String documentNumber,
    @NonNull DocumentCategory documentCategory
  ) {
    List<DocumentReference> referencedBy;
    switch (documentCategory) {
      case VERWALTUNGSVORSCHRIFTEN -> referencedBy = refViewActiveReferenceSliAdmRepository
        .findByTargetDocumentNumber(documentNumber)
        .stream()
        .map(rf ->
          new DocumentReference(
            rf.getSourceDocumentNumber(),
            DocumentCategory.LITERATUR_SELBSTAENDIG
          )
        )
        .toList();
      case LITERATUR_SELBSTAENDIG, LITERATUR_UNSELBSTAENDIG -> {
        referencedBy = new ArrayList<>();
        log.info("Passive references for literature is not supported.");
      }
      default -> referencedBy = new ArrayList<>();
    }
    return referencedBy;
  }
}
