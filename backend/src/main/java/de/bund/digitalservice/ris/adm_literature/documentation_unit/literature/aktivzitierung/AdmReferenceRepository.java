package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface AdmReferenceRepository
  extends JpaRepository<AdmReferenceEntity, String>, JpaSpecificationExecutor<AdmReferenceEntity> {
  default Page<AdmReferenceEntity> findAll(
    PredicateSpecification<AdmReferenceEntity> spec,
    Pageable pageable
  ) {
    return findAll(Specification.where(spec), pageable);
  }
}
