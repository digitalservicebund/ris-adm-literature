package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_vwv.application.Page;
import de.bund.digitalservice.ris.adm_vwv.application.QueryOptions;
import de.bund.digitalservice.ris.adm_vwv.test.TestFile;
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
      .createQuery(
        "from DocumentationUnitEntity where documentNumber = 'gibtsnicht'",
        DocumentationUnitEntity.class
      );
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
  void indexByDocumentationUnit_xml() {
    // given
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR9999999999");
    documentationUnitEntity.setXml(xml);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitPersistenceService.indexAll();

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = entityManager
      .getEntityManager()
      .createQuery(
        "from DocumentationUnitIndexEntity where documentationUnit = :documentationUnit",
        DocumentationUnitIndexEntity.class
      );
    query.setParameter("documentationUnit", documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(
        DocumentationUnitIndexEntity::getLangueberschrift,
        DocumentationUnitIndexEntity::getFundstellen,
        DocumentationUnitIndexEntity::getZitierdaten
      )
      .containsExactly(
        "1. Bekanntmachung zum XML-Testen in NeuRIS VwV",
        "Das Periodikum 2021, Seite 15",
        "2025-05-05 2025-06-01"
      );
  }

  @Test
  void indexByDocumentationUnit_json() {
    // given
    String json = TestFile.readFileToString("json-example.json");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR777777777");
    documentationUnitEntity.setJson(json);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitPersistenceService.indexAll();

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = entityManager
      .getEntityManager()
      .createQuery(
        "from DocumentationUnitIndexEntity where documentationUnit = :documentationUnit",
        DocumentationUnitIndexEntity.class
      );
    query.setParameter("documentationUnit", documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(
        DocumentationUnitIndexEntity::getLangueberschrift,
        DocumentationUnitIndexEntity::getFundstellen,
        DocumentationUnitIndexEntity::getZitierdaten
      )
      .containsExactly(
        "1. Bekanntmachung zum XML-Testen in NeuRIS VwV",
        "Das Periodikum 2021, Seite 15",
        "2025-05-05 2025-06-01"
      );
  }

  @Test
  void indexByDocumentationUnit_jsonAndXml() {
    // given
    String json = TestFile.readFileToString("json-example.json");
    String xml = TestFile.readFileToString("ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR555555555");
    documentationUnitEntity.setJson(json);
    documentationUnitEntity.setXml(xml);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitPersistenceService.indexAll();

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = entityManager
      .getEntityManager()
      .createQuery(
        "from DocumentationUnitIndexEntity where documentationUnit = :documentationUnit",
        DocumentationUnitIndexEntity.class
      );
    query.setParameter("documentationUnit", documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(
        DocumentationUnitIndexEntity::getLangueberschrift,
        DocumentationUnitIndexEntity::getFundstellen,
        DocumentationUnitIndexEntity::getZitierdaten
      )
      .containsExactly(
        "1. Bekanntmachung zum XML-Testen in NeuRIS VwV",
        "Das Periodikum 2021, Seite 15",
        "2025-05-05 2025-06-01"
      );
  }

  @Test
  void indexByDocumentationUnit_jsonAndXmlAreNull() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR333333333");
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitPersistenceService.indexAll();

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = entityManager
      .getEntityManager()
      .createQuery(
        "from DocumentationUnitIndexEntity where documentationUnit = :documentationUnit",
        DocumentationUnitIndexEntity.class
      );
    query.setParameter("documentationUnit", documentationUnitEntity);
    assertThat(query.getResultList()).isEmpty();
  }
}
