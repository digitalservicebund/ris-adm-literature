package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import de.bund.digitalservice.ris.adm_vwv.application.*;
import de.bund.digitalservice.ris.adm_vwv.test.*;
import jakarta.persistence.TypedQuery;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

// @DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @Import({
//     DocumentationUnitPersistenceService.class,
//     DocumentationUnitCreationService.class,
//     ObjectMapper.class,
//     LdmlConverterService.class
// })
@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
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

  @Test
  void findDocumentationUnitOverviewElements() {
    // given
    var documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber(String.format("KSNR%s100001", Year.now()));
    documentationUnitEntity.setJson(
      """
      {
        "id": "11111111-1657-4085-ae2a-993a04c27f6b",
        "documentNumber": "KSNR000004711",
        "zitierdatum": "2011-11-11",
        "langueberschrift": "Sample Document Title 1",
        "references": [
          {
            "id": "11111111-1fd3-4fb8-bc1d-9751ad192665",
            "citation": "zitatstelle 1",
            "legalPeriodical": {
              "id": "33333333-1fd3-4fb8-bc1d-9751ad192665",
              "title": "periodikum title 1",
              "subtitle": "periodikum subtitle 1",
              "abbreviation": "p.abbrev.1"
            }
          },
          {
            "id": "22222222-1fd3-4fb8-bc1d-9751ad192665",
            "citation": "zitatstelle 2",
            "legalPeriodical": {
              "id": "44444444-1fd3-4fb8-bc1d-9751ad192665",
              "title": "periodikum title 2",
              "subtitle": "periodikum subtitle 2",
              "abbreviation": "p.abbrev.2"
            }
          }
        ]
      }
      """
    );
    entityManager.persistAndFlush(documentationUnitEntity);

    // when
    var documentationUnitOverviewElements =
      documentationUnitPersistenceService.findDocumentationUnitOverviewElements(
        new QueryOptions(0, 10, "id", Sort.Direction.ASC, false)
      );

    // then
    assertThat(documentationUnitOverviewElements)
      .extracting(Page::content)
      .asInstanceOf(InstanceOfAssertFactories.list(DocumentationUnitOverviewElement.class))
      .singleElement()
      .extracting(
        DocumentationUnitOverviewElement::zitierdatum,
        DocumentationUnitOverviewElement::langueberschrift,
        documentationUnitOverviewElement ->
          documentationUnitOverviewElement
            .fundstellen()
            .stream()
            .map(fundstelle -> List.of(fundstelle.zitatstelle(), fundstelle.periodikum().title()))
            .toList()
      )
      .containsExactly(
        "2011-11-11",
        "Sample Document Title 1",
        List.of(
          List.of("zitatstelle 1", "periodikum title 1"),
          List.of("zitatstelle 2", "periodikum title 2")
        )
      );
  }

  @Test
  void findDocumentationUnitOverviewElements_withoutFundstellen() {
    // given
    var documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber(String.format("KSNR%s100002", Year.now()));
    documentationUnitEntity.setJson(
      """
      {
        "id": "11111111-1657-4085-ae2a-993a04c27f6b",
        "documentNumber": "KSNR000004711",
        "zitierdatum": "2011-11-11",
        "langueberschrift": "Sample Document Title 1"
      }
      """
    );
    entityManager.persistAndFlush(documentationUnitEntity);

    // when
    var documentationUnitOverviewElements =
      documentationUnitPersistenceService.findDocumentationUnitOverviewElements(
        new QueryOptions(0, 10, "id", Sort.Direction.ASC, false)
      );

    // then
    assertThat(documentationUnitOverviewElements)
      .extracting(Page::content)
      .asInstanceOf(InstanceOfAssertFactories.list(DocumentationUnitOverviewElement.class))
      .singleElement()
      .extracting(
        DocumentationUnitOverviewElement::zitierdatum,
        DocumentationUnitOverviewElement::langueberschrift,
        DocumentationUnitOverviewElement::fundstellen
      )
      .containsExactly("2011-11-11", "Sample Document Title 1", List.of());
  }

  @Test
  void findDocumentationUnitOverviewElements_parsingJsonFails() {
    // given
    var documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber(String.format("KSNR%s100003", Year.now()));
    documentationUnitEntity.setJson(
      """
      {
        "id": "11111111-1657-4085-ae2a-993a04c27f6b",
        "documentNumber": "KSNR000004711",
        [] ooops
      }
      """
    );
    entityManager.persistAndFlush(documentationUnitEntity);

    // when
    Exception exception = catchException(() ->
      documentationUnitPersistenceService.findDocumentationUnitOverviewElements(
        new QueryOptions(0, 10, "id", Sort.Direction.ASC, false)
      )
    );

    // then
    assertThat(exception).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void indexByDocumentationUnit() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR9999999999");
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);
    DocumentationUnit documentationUnit = new DocumentationUnit(
      documentationUnitEntity.getDocumentNumber(),
      documentationUnitEntity.getId(),
      null,
      xml
    );

    // when
    documentationUnitPersistenceService.index(documentationUnit);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = entityManager
      .getEntityManager()
      .createQuery("from DocumentationUnitIndexEntity", DocumentationUnitIndexEntity.class);
    assertThat(query.getResultList()).isNotEmpty();
  }
}
