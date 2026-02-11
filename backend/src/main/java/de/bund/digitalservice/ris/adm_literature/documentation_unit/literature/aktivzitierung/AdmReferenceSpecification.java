package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.RefViewAdmEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.util.StringUtils;

/**
 * Specification for searching Aktivzitierung within the ADM schema.
 *
 * @param documentNumber     The unique document identifier to filter by.
 * @param periodikum         The periodikum.
 * @param zitatstelle        The zitatstelle.
 * @param inkrafttretedatum  The effective date of the cited document.
 * @param aktenzeichen       The aktenzeichen.
 * @param dokumenttyp        The dokumenttyp.
 * @param normgeber          The normgeber.
 * @param zitierdatum        The zitierdatum
 */
public record AdmReferenceSpecification(
  String documentNumber,
  String periodikum,
  String zitatstelle,
  String inkrafttretedatum,
  String aktenzeichen,
  String dokumenttyp,
  String normgeber,
  String zitierdatum
)
  implements PredicateSpecification<@NonNull RefViewAdmEntity> {
  @Override
  public @Nullable Predicate toPredicate(
    From<?, RefViewAdmEntity> from,
    @NonNull CriteriaBuilder criteriaBuilder
  ) {
    List<Predicate> predicates = new ArrayList<>();
    addPredicate(predicates, from.get("documentNumber"), documentNumber, criteriaBuilder);
    var admIndex = from.get("admIndex");
    addPredicate(predicates, admIndex.get("fundstellenCombined"), periodikum, criteriaBuilder);
    addPredicate(predicates, admIndex.get("fundstellenCombined"), zitatstelle, criteriaBuilder);
    addPredicate(predicates, admIndex.get("inkrafttretedatum"), inkrafttretedatum, criteriaBuilder);
    addPredicate(
      predicates,
      admIndex.get("aktenzeichenListCombined"),
      aktenzeichen,
      criteriaBuilder
    );
    addPredicate(predicates, admIndex.get("dokumenttyp"), dokumenttyp, criteriaBuilder);
    addPredicate(predicates, admIndex.get("normgeberListCombined"), normgeber, criteriaBuilder);
    addPredicate(predicates, admIndex.get("zitierdatenCombined"), zitierdatum, criteriaBuilder);
    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private void addPredicate(
    @NonNull List<Predicate> predicates,
    @NonNull Path<String> path,
    String searchTerm,
    @NonNull CriteriaBuilder criteriaBuilder
  ) {
    if (StringUtils.hasText(searchTerm)) {
      predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), sqlContains(searchTerm)));
    }
  }

  private String sqlContains(String term) {
    return "%" + term.toLowerCase() + "%";
  }
}
