package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

interface ActiveReferenceAdmRepository extends JpaRepository<ActiveReferenceAdmEntity, UUID> {
  List<ActiveReferenceAdmEntity> findBySourceDocumentationUnit(
    @NonNull DocumentationUnitEntity sourceDocumentationUnit
  );
}
