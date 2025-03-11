package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DocumentTypesRepository extends JpaRepository<DocumentTypeEntity, UUID> {
  List<DocumentTypeEntity> findByAbbreviationLikeIgnoreCaseOrNameLikeIgnoreCase(
    String abbreviation, String name
  );
}
