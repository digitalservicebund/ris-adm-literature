package de.bund.digitalservice.ris.adm_literature.documentation_unit.notes;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Note service for storing notes for documentation units.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class NoteService {

  private final NoteRepository noteRepository;

  /**
   * Saves the given note for the given documentation unit entity. If the given note is {@code null} or blank,
   * then an existing note is deleted.
   * @param documentationUnitEntity The documentation unit entity
   * @param note The note to save (can be {@code null} or blank, then an existing note is removed)
   */
  @Transactional
  public void save(@Nonnull DocumentationUnitEntity documentationUnitEntity, String note) {
    if (StringUtils.isBlank(note)) {
      // Remove note entity if given string is null or blank
      noteRepository
        .findByDocumentationUnit(documentationUnitEntity)
        .ifPresent(noteRepository::delete);
      log.info(
        "Deleted note for document number: {}.",
        documentationUnitEntity.getDocumentNumber()
      );
    } else {
      // Create new note or update existing one
      NoteEntity noteEntity = noteRepository
        .findByDocumentationUnit(documentationUnitEntity)
        .orElseGet(() -> {
          var newNoteEntity = new NoteEntity();
          newNoteEntity.setDocumentationUnit(documentationUnitEntity);
          return newNoteEntity;
        });
      noteEntity.setNote(note);
      if (noteEntity.getId() == null) {
        noteRepository.save(noteEntity);
      }
      log.info("Saved note for document number: {}.", documentationUnitEntity.getDocumentNumber());
    }
  }

  /**
   * Returns an optional note string for the given documentation unit entity.
   * @param documentationUnitEntity The documentation unit entity
   * @return Optional note string
   */
  @Transactional(readOnly = true)
  public String find(@Nonnull DocumentationUnitEntity documentationUnitEntity) {
    return noteRepository
      .findByDocumentationUnit(documentationUnitEntity)
      .map(NoteEntity::getNote)
      .orElse(null);
  }
}
