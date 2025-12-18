package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Specification for searching Aktivzitierung within the ADM schema.
 *
 * @param documentNumber     The unique document identifier to filter by.
 * @param periodikum         The periodikum.
 * @param zitatstelle        The zitatselle.
 * @param inkrafttretedatum  The effective date of the cited document.
 * @param aktenzeichen       The aktenzeichen.
 * @param dokumenttyp        The dokumenttyp.
 * @param normgeber          The normgeber.
 */
public record AktivzitierungAdmSpecification(
  String documentNumber,
  String periodikum,
  String zitatstelle,
  String inkrafttretedatum,
  String aktenzeichen,
  String dokumenttyp,
  String normgeber
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

    var admIndex = root.join("documentationUnitIndex", JoinType.LEFT).get("admIndex");

    if (StringUtils.hasText(periodikum)) {
      predicates.add(
        criteriaBuilder.like(
          criteriaBuilder.lower(admIndex.get("fundstellenCombined")),
          sqlContains(periodikum)
        )
      );
    }

    if (StringUtils.hasText(zitatstelle)) {
      predicates.add(
        criteriaBuilder.like(
          criteriaBuilder.lower(admIndex.get("zitierdatenCombined")),
          sqlContains(zitatstelle)
        )
      );
    }

    if (StringUtils.hasText(inkrafttretedatum)) {
      predicates.add(
        criteriaBuilder.like(
          criteriaBuilder.lower(admIndex.get("inkrafttretedatum")),
          sqlContains(inkrafttretedatum)
        )
      );
    }

    if (StringUtils.hasText(aktenzeichen)) {
      predicates.add(
        criteriaBuilder.like(
          criteriaBuilder.lower(admIndex.get("aktenzeichenListCombined")),
          sqlContains(aktenzeichen)
        )
      );
    }

    if (StringUtils.hasText(dokumenttyp)) {
      predicates.add(
        criteriaBuilder.like(
          criteriaBuilder.lower(admIndex.get("dokumenttyp")),
          sqlContains(dokumenttyp)
        )
      );
    }

    if (StringUtils.hasText(normgeber)) {
      predicates.add(
        criteriaBuilder.like(
          criteriaBuilder.lower(admIndex.get("normgeberListCombined")),
          sqlContains(normgeber)
        )
      );
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private String sqlContains(String term) {
    return "%" + term.toLowerCase() + "%";
  }
}
