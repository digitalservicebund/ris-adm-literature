package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentReferenceRepository extends JpaRepository<DocumentReferenceEntity, UUID> {}
