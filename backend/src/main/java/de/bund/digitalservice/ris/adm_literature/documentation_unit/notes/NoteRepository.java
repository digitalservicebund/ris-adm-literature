package de.bund.digitalservice.ris.adm_literature.documentation_unit.notes;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface NoteRepository extends JpaRepository<NoteEntity, UUID> {
  Optional<NoteEntity> findByDocumentationUnit(
    @Nonnull DocumentationUnitEntity documentationUnitEntity
  );
}
