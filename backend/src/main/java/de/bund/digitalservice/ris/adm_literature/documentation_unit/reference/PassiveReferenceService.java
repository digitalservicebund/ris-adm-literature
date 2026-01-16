package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Passive reference service for reading passive references.
 */
@RequiredArgsConstructor
@Service
public class PassiveReferenceService {

  private final AdmPassiveReferenceRepository admPassiveReferenceRepository;

  /**
   * Returns all passive references for the given document category.
   *
   * @param documentCategory The document category
   * @return Passive references
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public List<PassiveReference> findAll(@NonNull DocumentCategory documentCategory) {
    List<PassiveReference> passiveReferences;
    switch (documentCategory) {
      case VERWALTUNGSVORSCHRIFTEN -> passiveReferences = admPassiveReferenceRepository
        .findAll()
        .stream()
        .map(apr ->
          new PassiveReference(
            new DocumentReference(
              apr.getTargetDocumentNumber(),
              DocumentCategory.VERWALTUNGSVORSCHRIFTEN
            ),
            new DocumentReference(apr.getSourceDocumentNumber(), apr.getSourceDocumentCategory())
          )
        )
        .toList();
      default -> passiveReferences = new ArrayList<>();
    }
    return passiveReferences;
  }
}
