package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Jpa repository for {@link RefViewLiteratureEntity}.
 */
public interface RefViewLiteratureRepository
  extends
    JpaRepository<RefViewLiteratureEntity, String>,
    JpaSpecificationExecutor<RefViewLiteratureEntity> {
  default Page<RefViewLiteratureEntity> findAll(
    PredicateSpecification<RefViewLiteratureEntity> spec,
    Pageable pageable
  ) {
    return findAll(Specification.where(spec), pageable);
  }
}
