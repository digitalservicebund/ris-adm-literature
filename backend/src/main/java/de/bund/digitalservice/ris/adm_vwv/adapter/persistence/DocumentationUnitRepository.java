package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentationUnitRepository extends JpaRepository<DocumentationUnitEntity, UUID> {}
