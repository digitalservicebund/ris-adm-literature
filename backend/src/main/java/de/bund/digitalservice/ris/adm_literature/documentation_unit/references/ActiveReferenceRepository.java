package de.bund.digitalservice.ris.adm_literature.documentation_unit.references;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface ActiveReferenceRepository extends JpaRepository<ActiveReferenceEntity, UUID> {}
