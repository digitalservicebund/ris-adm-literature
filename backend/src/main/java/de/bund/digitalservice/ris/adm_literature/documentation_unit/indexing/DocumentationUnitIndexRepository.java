package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentationUnitIndexRepository
  extends JpaRepository<DocumentationUnitIndexEntity, UUID> {}
