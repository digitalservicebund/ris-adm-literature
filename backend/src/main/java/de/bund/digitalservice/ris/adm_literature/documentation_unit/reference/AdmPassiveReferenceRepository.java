package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

interface AdmPassiveReferenceRepository extends JpaRepository<AdmPassiveReferenceEntity, UUID> {
  List<AdmPassiveReferenceEntity> findByTargetDocumentNumber(@NonNull String targetDocumentNumber);
}
