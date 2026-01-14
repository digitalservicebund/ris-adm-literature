package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PassiveReferenceService {

  private final AdmPassiveReferenceRepository admPassiveReferenceRepository;

  public List<PassiveReference> findPassiveReferences(@NonNull DocumentCategory documentCategory) {
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
