package de.bund.digitalservice.ris.adm_literature.documentation_unit.passivzitierung;

import static java.util.stream.Collectors.*;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaContextHolder;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaExecutor;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.PassiveReference;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.PassiveReferenceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * Service for updating 'Passivzitierungen' on a regular basis.
 */
@Service
@RequiredArgsConstructor
public class UpdatePassivzitierungService {

  private final PassiveReferenceService passiveReferenceService;
  private final SchemaExecutor schemaExecutor;
  private final DocumentationUnitService documentationUnitService;

  /**
   * Updates all 'Passivzitierungen'.
   *
   * @param documentCategory The document category to update
   */
  public void updateAllByDocumentCategory(@NonNull DocumentCategory documentCategory) {
    List<PassiveReference> passiveReferences = schemaExecutor.executeInSchema(
      SchemaType.REFERENCES,
      () -> passiveReferenceService.findAll(documentCategory)
    );
    SchemaContextHolder.setSchema(documentCategory.getSchemaType());
    passiveReferences
      .stream()
      .collect(
        groupingBy(PassiveReference::target, mapping(PassiveReference::referencedBy, toList()))
      )
      .forEach((target, referencedByList) ->
        documentationUnitService.publishPassiveReferences(target.documentNumber(), referencedByList)
      );
  }
}
