package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public record LiteratureDocumentationUnitSpecification(
  String documentNumber,
  String veroeffentlichungsjahr,
  List<DocumentType> dokumenttypen,
  String titel,
  List<String> verfasser
)
  implements Specification<DocumentationUnitEntity> {
  @Override
  public Predicate toPredicate(
    @Nonnull Root<DocumentationUnitEntity> root,
    CriteriaQuery<?> query,
    @Nonnull CriteriaBuilder criteriaBuilder
  ) {
    ArrayList<Predicate> predicates = new ArrayList<>();

    if (StringUtils.hasText(documentNumber)) {
      predicates.add(
        criteriaBuilder.like(
          criteriaBuilder.lower(root.get("documentNumber")),
          sqlContains(documentNumber)
        )
      );
    }

    boolean searchInIndexTable =
      StringUtils.hasText(veroeffentlichungsjahr) ||
      StringUtils.hasText(titel) ||
      !CollectionUtils.isEmpty(dokumenttypen) ||
      !CollectionUtils.isEmpty(verfasser);

    if (searchInIndexTable) {
      Join<DocumentationUnitEntity, DocumentationUnitIndexEntity> indexJoin = root.join(
        "documentationUnitIndex",
        JoinType.LEFT
      );

      if (StringUtils.hasText(veroeffentlichungsjahr)) {
        predicates.add(
          criteriaBuilder.like(
            criteriaBuilder.lower(indexJoin.get("veroeffentlichungsjahr")),
            sqlContains(veroeffentlichungsjahr)
          )
        );
      }

      if (StringUtils.hasText(titel)) {
        predicates.add(
          criteriaBuilder.like(criteriaBuilder.lower(indexJoin.get("titel")), sqlContains(titel))
        );
      }

      if (!CollectionUtils.isEmpty(dokumenttypen)) {
        Predicate[] typePredicates = dokumenttypen
          .stream()
          .map(type ->
            criteriaBuilder.like(
              criteriaBuilder.lower(indexJoin.get("dokumenttypen")),
              sqlContains(type.abbreviation())
            )
          )
          .toArray(Predicate[]::new);

        predicates.add(criteriaBuilder.or(typePredicates));
      }

      if (!CollectionUtils.isEmpty(verfasser)) {
        Predicate[] authorPredicates = verfasser
          .stream()
          .map(author ->
            criteriaBuilder.like(
              criteriaBuilder.lower(indexJoin.get("verfasser")),
              sqlContains(author)
            )
          )
          .toArray(Predicate[]::new);

        predicates.add(criteriaBuilder.or(authorPredicates));
      }
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private String sqlContains(String term) {
    return "%" + term.toLowerCase() + "%";
  }
}
