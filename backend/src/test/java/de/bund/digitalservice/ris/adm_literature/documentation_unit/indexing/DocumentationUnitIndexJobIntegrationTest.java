package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import de.bund.digitalservice.ris.adm_literature.test.TestFile;
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
class DocumentationUnitIndexJobIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private DocumentationUnitIndexJob documentationUnitIndexJob;

  @Test
  void indexAll() {
    // given
    entityManager
      .getEntityManager()
      .createQuery("DELETE FROM DocumentationUnitIndexEntity")
      .executeUpdate();
    entityManager
      .getEntityManager()
      .createQuery("DELETE FROM DocumentationUnitEntity")
      .executeUpdate();
    DocumentationUnitEntity admDocumentationUnitEntity = new DocumentationUnitEntity();
    admDocumentationUnitEntity.setDocumentNumber("KSNR777777777");
    admDocumentationUnitEntity.setJson(TestFile.readFileToString("adm/json-example.json"));
    admDocumentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    admDocumentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    entityManager.persistAndFlush(admDocumentationUnitEntity);

    // when
    documentationUnitIndexJob.indexAll();

    // then
    assertThat(
      entityManager
        .getEntityManager()
        .createQuery("from DocumentationUnitIndexEntity", DocumentationUnitIndexEntity.class)
        .getResultList()
    ).hasSize(1);
  }
}
