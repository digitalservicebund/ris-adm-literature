package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Jpa repository for {@link RefViewActiveReferenceSliAdmEntity}.
 */
public interface RefViewActiveReferenceSliAdmRepository
  extends
    JpaRepository<RefViewActiveReferenceSliAdmEntity, UUID>,
    JpaSpecificationExecutor<RefViewActiveReferenceSliAdmEntity> {
  default Page<RefViewActiveReferenceSliAdmEntity> findAll(
    PredicateSpecification<RefViewActiveReferenceSliAdmEntity> spec,
    Pageable pageable
  ) {
    return findAll(Specification.where(spec), pageable);
  }

  List<RefViewActiveReferenceSliAdmEntity> findByTargetDocumentationUnitId(
    @NonNull UUID targetDocumentationUnitId
  );
}
