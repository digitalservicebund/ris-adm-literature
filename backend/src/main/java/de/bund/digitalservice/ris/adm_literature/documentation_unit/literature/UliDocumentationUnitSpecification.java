package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * A specification class that defines query filters for the {@link DocumentationUnitEntity} entity.
 * It uses various criteria for querying data such as document number, year of publication,
 * document types, title, and authors. This class is part of the unit specification for
 * querying and filtering LiteratureDocumentation.
 *
 * @param documentNumber     A string representing the document number to filter by.
 * @param periodikum         A string representing the periodical to filter by.
 * @param zitatstelle        A string representing the citation from the periodical to filter by.
 * @param dokumenttypen      A list of {@link DocumentType} objects representing the document types to filter by.
 * @param verfasser          A list of strings representing the authors to filter by.
 */
public record UliDocumentationUnitSpecification(
  String documentNumber,
  String periodikum,
  String zitatstelle,
  List<String> dokumenttypen,
  List<String> verfasser
)
  implements Specification<DocumentationUnitEntity> {
  @Override
  public Predicate toPredicate(
    @Nonnull Root<DocumentationUnitEntity> root,
    CriteriaQuery<?> query,
    @Nonnull CriteriaBuilder criteriaBuilder
  ) {
    List<Predicate> predicates = new ArrayList<>();

    // Base filters: must be ULI and published
    predicates.add(
      criteriaBuilder.equal(
        root.get("documentationUnitType"),
        DocumentCategory.LITERATUR_UNSELBSTAENDIG.name()
      )
    );
    predicates.add(criteriaBuilder.isNotNull(root.get("xml")));

    // documentNumber
    addLikePredicate(predicates, criteriaBuilder, root.get("documentNumber"), documentNumber);

    var index = root.join("documentationUnitIndex", JoinType.LEFT);
    var litIndex = root.join("documentationUnitIndex", JoinType.LEFT).get("literatureIndex");

    // periodikum
    addLikePredicate(predicates, criteriaBuilder, index.get("fundstellenCombined"), periodikum);
    // zitatstelle
    addLikePredicate(predicates, criteriaBuilder, index.get("fundstellenCombined"), zitatstelle);

    // dokumenttypen
    if (!CollectionUtils.isEmpty(dokumenttypen)) {
      predicates.add(
        buildCollectionAndPredicate(
          criteriaBuilder,
          litIndex.get("dokumenttypenCombined"),
          dokumenttypen
        )
      );
    }

    // verfasser
    if (!CollectionUtils.isEmpty(verfasser)) {
      predicates.add(
        buildCollectionAndPredicate(
          criteriaBuilder,
          litIndex.get("verfasserListCombined"),
          verfasser
        )
      );
    }

    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
  }

  private void addLikePredicate(
    List<Predicate> predicates,
    CriteriaBuilder cb,
    Expression<String> path,
    String term
  ) {
    if (StringUtils.hasText(term)) {
      predicates.add(cb.like(cb.lower(path), "%" + term.toLowerCase() + "%"));
    }
  }

  private Predicate buildCollectionAndPredicate(
    CriteriaBuilder cb,
    Expression<String> path,
    List<String> terms
  ) {
    return cb.and(
      terms
        .stream()
        .filter(StringUtils::hasText)
        .map(term -> cb.like(cb.lower(path), "%" + term.toLowerCase() + "%"))
        .toArray(Predicate[]::new)
    );
  }
}
