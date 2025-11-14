package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentationUnitIndexRepository
  extends JpaRepository<DocumentationUnitIndexEntity, UUID> {}
