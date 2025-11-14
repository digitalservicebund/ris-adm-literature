package de.bund.digitalservice.ris.adm_literature.adapter.persistence;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * JPA specification for querying documentation units by documentNumber, langueberschrift and fundstellen.
 */
@RequiredArgsConstructor
public class DocumentUnitSpecification implements Specification<DocumentationUnitEntity> {

  private final String documentNumber;
  private final String langueberschrift;
  private final String fundstellen;
  private final String zitierdaten;

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
    if (
      StringUtils.hasText(fundstellen) ||
      StringUtils.hasText(langueberschrift) ||
      StringUtils.hasText(zitierdaten)
    ) {
      Join<DocumentationUnitEntity, DocumentationUnitIndexEntity> indexJoin = root.join(
        "documentationUnitIndex",
        JoinType.LEFT
      );

      if (StringUtils.hasText(fundstellen)) {
        predicates.add(
          criteriaBuilder.like(
            criteriaBuilder.lower(indexJoin.get("fundstellen")),
            sqlContains(fundstellen)
          )
        );
      }
      if (StringUtils.hasText(langueberschrift)) {
        predicates.add(
          criteriaBuilder.like(
            criteriaBuilder.lower(indexJoin.get("langueberschrift")),
            sqlContains(langueberschrift)
          )
        );
      }
      if (StringUtils.hasText(zitierdaten)) {
        predicates.add(
          criteriaBuilder.like(
            criteriaBuilder.lower(indexJoin.get("zitierdaten")),
            sqlContains(zitierdaten)
          )
        );
      }
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private String sqlContains(String term) {
    return "%" + term.toLowerCase() + "%";
  }
}
