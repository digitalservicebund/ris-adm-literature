package de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law.FieldOfLaw.FieldOfLawBuilder;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
class FieldOfLawTransformer {

  public static FieldOfLaw transformToDomain(
    FieldOfLawEntity fieldOfLawEntity,
    boolean withChildren,
    boolean withNorms
  ) {
    FieldOfLawBuilder builder = FieldOfLaw.builder()
      .id(fieldOfLawEntity.getId())
      .identifier(fieldOfLawEntity.getIdentifier())
      .notation(fieldOfLawEntity.getNotation())
      .text(fieldOfLawEntity.getText());
    if (fieldOfLawEntity.getParent() != null) {
      builder.parent(transformToDomain(fieldOfLawEntity.getParent(), false, false));
    }
    builder.hasChildren(!fieldOfLawEntity.getChildren().isEmpty());
    if (withChildren) {
      List<FieldOfLaw> children = fieldOfLawEntity
        .getChildren()
        .stream()
        .map(fol -> FieldOfLawTransformer.transformToDomain(fol, false, withNorms))
        .toList();
      builder.children(children);
      builder.hasChildren(true);
    } else {
      builder.children(Collections.emptyList());
    }
    if (withNorms) {
      List<FieldOfLawNorm> norms = fieldOfLawEntity
        .getNorms()
        .stream()
        .map(fieldOfLawNormEntity ->
          FieldOfLawNorm.builder()
            .abbreviation(fieldOfLawNormEntity.getAbbreviation())
            .singleNormDescription(fieldOfLawNormEntity.getSingleNormDescription())
            .build()
        )
        .toList();
      builder.norms(norms);
    }
    List<String> linkedFields = fieldOfLawEntity
      .getFieldOfLawTextReferences()
      .stream()
      .map(FieldOfLawEntity::getIdentifier)
      .toList();
    builder.linkedFields(linkedFields);
    return builder.build();
  }
}
