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

    addPredicate(documentNumber, predicates, criteriaBuilder, root.get("documentNumber"));

    var admIndex = root.join("documentationUnitIndex", JoinType.LEFT).get("admIndex");

    addPredicate(periodikum, predicates, criteriaBuilder, admIndex.get("fundstellenCombined"));
    addPredicate(zitatstelle, predicates, criteriaBuilder, admIndex.get("fundstellenCombined"));

    addPredicate(inkrafttretedatum, predicates, criteriaBuilder, admIndex.get("inkrafttretedatum"));
    addPredicate(
      aktenzeichen,
      predicates,
      criteriaBuilder,
      admIndex.get("aktenzeichenListCombined")
    );
    addPredicate(dokumenttyp, predicates, criteriaBuilder, admIndex.get("dokumenttyp"));
    addPredicate(normgeber, predicates, criteriaBuilder, admIndex.get("normgeberListCombined"));

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private void addPredicate(
    String param,
    ArrayList<Predicate> predicates,
    @Nonnull CriteriaBuilder criteriaBuilder,
    Path<String> root
  ) {
    if (StringUtils.hasText(param)) {
      predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root), sqlContains(param)));
    }
  }

  private String sqlContains(String term) {
    return "%" + term.toLowerCase() + "%";
  }
}
