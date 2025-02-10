package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface DocumentationUnitRepository extends JpaRepository<DocumentationUnitEntity, UUID> {
}
