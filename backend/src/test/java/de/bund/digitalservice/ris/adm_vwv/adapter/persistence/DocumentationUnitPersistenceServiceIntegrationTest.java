package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.*;
import de.bund.digitalservice.ris.adm_vwv.test.TestFile;
import jakarta.persistence.TypedQuery;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentationUnitPersistenceService.ENTRY_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

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
    documentationUnitEntity.setDocumentNumber(String.format("KSNR%s000002", thisYear));
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
      "{\"test\":\"content\"}"
    );

    // then
    assertThat(entityManager.find(DocumentationUnitEntity.class, id))
      .extracting(DocumentationUnitEntity::getJson)
      .isEqualTo("{\"test\":\"content\"}");
  }

  @Test
  void update_reindex() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR111111111");
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);
    DocumentationUnitIndexEntity documentationUnitIndexEntity = new DocumentationUnitIndexEntity();
    documentationUnitIndexEntity.setDocumentationUnit(documentationUnitEntity);
    documentationUnitIndexEntity.setLangueberschrift("Lang");
    documentationUnitIndexEntity.setFundstellen("Fund");
    documentationUnitIndexEntity.setZitierdaten("2012-12-12");
    entityManager.persistAndFlush(documentationUnitIndexEntity);
    String json = TestFile.readFileToString("json-example.json");

    // when
    documentationUnitPersistenceService.update(documentationUnitEntity.getDocumentNumber(), json);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
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
        "2025-05-05%s2025-06-01".formatted(ENTRY_SEPARATOR)
      );
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
    documentationUnitEntity.setDocumentNumber("KSNR2025100001");
    documentationUnitEntity.setJson(
      """
      {
        "id": "11111111-1657-4085-ae2a-993a04c27f6b",
        "documentNumber": "KSNR000004711",
        "zitierdaten": [ "2011-11-11" ],
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
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);
    DocumentationUnitIndexEntity documentationUnitIndexEntity = new DocumentationUnitIndexEntity();
    documentationUnitIndexEntity.setDocumentationUnit(documentationUnitEntity);
    documentationUnitIndexEntity.setLangueberschrift("Sample Document Title 1");
    documentationUnitIndexEntity.setFundstellen(
      "p.abbrev.1 zitatstelle 1%sp.abbrev.2 zitatstelle 2".formatted(ENTRY_SEPARATOR)
    );
    documentationUnitIndexEntity.setZitierdaten("2011-11-11");
    entityManager.persistAndFlush(documentationUnitIndexEntity);

    // when
    var documentationUnitOverviewElements =
      documentationUnitPersistenceService.findDocumentationUnitOverviewElements(
        new DocumentationUnitQuery(
          null,
          null,
          null,
          null,
          new QueryOptions(0, 10, "id", Sort.Direction.ASC, false)
        )
      );

    // then
    assertThat(documentationUnitOverviewElements)
      .extracting(Page::content)
      .asInstanceOf(InstanceOfAssertFactories.list(DocumentationUnitOverviewElement.class))
      .filteredOn(documentationUnitOverviewElement ->
        documentationUnitOverviewElement.documentNumber().equals("KSNR2025100001")
      )
      .singleElement()
      .extracting(
        DocumentationUnitOverviewElement::zitierdaten,
        DocumentationUnitOverviewElement::langueberschrift,
        DocumentationUnitOverviewElement::fundstellen
      )
      .containsExactly(
        List.of("2011-11-11"),
        "Sample Document Title 1",
        List.of("p.abbrev.1 zitatstelle 1", "p.abbrev.2 zitatstelle 2")
      );
  }

  @Test
  void findDocumentationUnitOverviewElements_withoutFundstellen() {
    // given
    var documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR2025100002");
    documentationUnitEntity.setJson(
      """
      {
        "id": "11111111-1657-4085-ae2a-993a04c27f6b",
        "documentNumber": "KSNR2025100002",
        "zitierdatum": "2011-11-11",
        "langueberschrift": "Sample Document Title 1"
      }
      """
    );
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);
    DocumentationUnitIndexEntity documentationUnitIndexEntity = new DocumentationUnitIndexEntity();
    documentationUnitIndexEntity.setDocumentationUnit(documentationUnitEntity);
    documentationUnitIndexEntity.setLangueberschrift("Sample Document Title 1");
    documentationUnitIndexEntity.setZitierdaten("2011-11-11");
    entityManager.persistAndFlush(documentationUnitIndexEntity);

    // when
    var documentationUnitOverviewElements =
      documentationUnitPersistenceService.findDocumentationUnitOverviewElements(
        new DocumentationUnitQuery(
          null,
          null,
          null,
          null,
          new QueryOptions(0, 10, "id", Sort.Direction.ASC, false)
        )
      );

    // then
    assertThat(documentationUnitOverviewElements)
      .extracting(Page::content)
      .asInstanceOf(InstanceOfAssertFactories.list(DocumentationUnitOverviewElement.class))
      .filteredOn(documentationUnitOverviewElement ->
        documentationUnitOverviewElement.documentNumber().equals("KSNR2025100002")
      )
      .singleElement()
      .extracting(
        DocumentationUnitOverviewElement::zitierdaten,
        DocumentationUnitOverviewElement::langueberschrift,
        DocumentationUnitOverviewElement::fundstellen
      )
      .containsExactly(List.of("2011-11-11"), "Sample Document Title 1", List.of());
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
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
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
        "2025-05-05%s2025-06-01".formatted(ENTRY_SEPARATOR)
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
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
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
        "2025-05-05%s2025-06-01".formatted(ENTRY_SEPARATOR)
      );
  }

  @Test
  void indexByDocumentationUnit_jsonNotValid() {
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
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitPersistenceService.indexAll();

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(
        DocumentationUnitIndexEntity::getLangueberschrift,
        DocumentationUnitIndexEntity::getFundstellen,
        DocumentationUnitIndexEntity::getZitierdaten
      )
      .containsExactly(null, null, null);
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
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
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
        "2025-05-05%s2025-06-01".formatted(ENTRY_SEPARATOR)
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
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(
        DocumentationUnitIndexEntity::getLangueberschrift,
        DocumentationUnitIndexEntity::getFundstellen,
        DocumentationUnitIndexEntity::getZitierdaten
      )
      .containsExactly(null, null, null);
  }

  private TypedQuery<DocumentationUnitIndexEntity> createTypedQuery(
    DocumentationUnitEntity documentationUnitEntity
  ) {
    TypedQuery<DocumentationUnitIndexEntity> query = entityManager
      .getEntityManager()
      .createQuery(
        "from DocumentationUnitIndexEntity where documentationUnit = :documentationUnit",
        DocumentationUnitIndexEntity.class
      );
    query.setParameter("documentationUnit", documentationUnitEntity);
    return query;
  }
}
