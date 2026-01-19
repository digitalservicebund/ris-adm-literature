package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.aktivzitierung;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Literature reference JPA repository.
 */
public interface LiteratureReferenceRepository
  extends
    JpaRepository<LiteratureReferenceEntity, String>,
    JpaSpecificationExecutor<LiteratureReferenceEntity> {
  default Page<LiteratureReferenceEntity> findAll(
    PredicateSpecification<LiteratureReferenceEntity> spec,
    Pageable pageable
  ) {
    return findAll(Specification.where(spec), pageable);
  }
}
