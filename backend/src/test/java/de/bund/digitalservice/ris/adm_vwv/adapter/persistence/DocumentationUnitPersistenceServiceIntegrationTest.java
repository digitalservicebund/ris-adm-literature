package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.TestcontainersConfiguration;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(
  {
    TestcontainersConfiguration.class,
    DocumentationUnitPersistenceService.class,
    DocumentationUnitCreationService.class,
  }
)
class DocumentationUnitPersistenceServiceIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private DocumentationUnitPersistenceService documentationUnitPersistenceService;

  @Test
  void findByDocumentNumber() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    Year thisYear = Year.now();
    documentationUnitEntity.setDocumentNumber(String.format("KSNR%s000001", thisYear));
    documentationUnitEntity.setJson("{\"test\":\"content\"");
    entityManager.persistAndFlush(documentationUnitEntity);

    // when
    Optional<DocumentationUnit> documentationUnit =
      documentationUnitPersistenceService.findByDocumentNumber(
        documentationUnitEntity.getDocumentNumber()
      );

    // then
    assertThat(documentationUnit)
      .isPresent()
      .hasValueSatisfying(actual -> assertThat(actual.json()).isEqualTo("{\"test\":\"content\""));
  }

  @Test
  void create() {
    // given

    // when
    DocumentationUnit documentationUnit = documentationUnitPersistenceService.create();

    // then
    assertThat(
      entityManager.find(DocumentationUnitEntity.class, documentationUnit.id())
    ).isNotNull();
  }

  @Test
  void update() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    Year thisYear = Year.now();
    documentationUnitEntity.setDocumentNumber(String.format("KSNR%s000001", thisYear));
    UUID id = entityManager.persistFlushFind(documentationUnitEntity).getId();

    // when
    documentationUnitPersistenceService.update(
      documentationUnitEntity.getDocumentNumber(),
      "{\"test\":\"content\""
    );

    // then
    assertThat(entityManager.find(DocumentationUnitEntity.class, id))
      .extracting(DocumentationUnitEntity::getJson)
      .isEqualTo("{\"test\":\"content\"");
  }

  @Test
  void update_notFound() {
    // given

    // when
    documentationUnitPersistenceService.update("gibtsnicht", "{\"test\":\"content\"");

    // then
    TypedQuery<DocumentationUnitEntity> query = entityManager
      .getEntityManager()
      .createQuery("from DocumentationUnitEntity", DocumentationUnitEntity.class);
    assertThat(query.getResultList()).isEmpty();
  }
}
