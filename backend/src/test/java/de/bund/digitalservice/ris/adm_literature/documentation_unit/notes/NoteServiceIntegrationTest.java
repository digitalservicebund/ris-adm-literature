package de.bund.digitalservice.ris.adm_literature.documentation_unit.notes;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
class NoteServiceIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private NoteService noteService;

  @Test
  @DisplayName(
    "Save a new note for a documentation unit and expects it on reading from the database"
  )
  void save() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KVLU2025001234");
    documentationUnitEntity.setJson("{\"test\":\"content\"}");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BVERFG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    noteService.save(documentationUnitEntity, "Keine Anmerkung");

    // then
    assertThat(
      entityManager
        .getEntityManager()
        .createQuery("from NoteEntity", NoteEntity.class)
        .getResultList()
    )
      .singleElement()
      .extracting(NoteEntity::getNote)
      .isEqualTo("Keine Anmerkung");
  }

  @Test
  @DisplayName(
    "Save an updated note for a documentation unit and expects it on reading from the database"
  )
  void save_updateExistingNote() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KVLU2025001234");
    documentationUnitEntity.setJson("{\"test\":\"content\"}");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BVERFG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);
    NoteEntity noteEntity = new NoteEntity();
    noteEntity.setNote("Notiz");
    noteEntity.setDocumentationUnit(documentationUnitEntity);
    entityManager.persistAndFlush(noteEntity);

    // when
    noteService.save(documentationUnitEntity, "Verfasser fehlen");

    // then
    assertThat(
      entityManager
        .getEntityManager()
        .createQuery("from NoteEntity", NoteEntity.class)
        .getResultList()
    )
      .singleElement()
      .extracting(NoteEntity::getNote)
      .isEqualTo("Verfasser fehlen");
  }

  @Test
  @DisplayName(
    "Save a blank note for a documentation unit and expects the existing note to be deleted"
  )
  void save_deleteBlankNote() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KVLU2025001234");
    documentationUnitEntity.setJson("{\"test\":\"content\"}");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BVERFG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);
    NoteEntity noteEntity = new NoteEntity();
    noteEntity.setNote("Notiz");
    noteEntity.setDocumentationUnit(documentationUnitEntity);
    entityManager.persistAndFlush(noteEntity);

    // when
    noteService.save(documentationUnitEntity, "   ");

    // then
    assertThat(
      entityManager
        .getEntityManager()
        .createQuery("from NoteEntity", NoteEntity.class)
        .getResultList()
    ).isEmpty();
  }
}
