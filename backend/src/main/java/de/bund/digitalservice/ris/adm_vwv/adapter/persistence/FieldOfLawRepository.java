package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldOfLawRepository extends JpaRepository<FieldOfLawEntity, UUID> {
  Optional<FieldOfLawEntity> findByIdentifier(String identifier);

  List<FieldOfLawEntity> findByParentIsNullAndNotationOrderByIdentifier(String notation);

}
