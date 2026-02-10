package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.AdmIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.DocumentationUnitIndexEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.LiteratureIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.notes.NoteEntity;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import de.bund.digitalservice.ris.adm_literature.test.TestFile;
import de.bund.digitalservice.ris.adm_literature.test.WithMockAdmUser;
import jakarta.persistence.TypedQuery;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
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

  private void createTestUnit(
    String documentNumber,
    String langueberschrift,
    String fundstellen,
    String zitierdaten
  ) {
    var unit = new DocumentationUnitEntity();
    unit.setDocumentNumber(documentNumber);
    unit.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    unit.setDocumentationOffice(DocumentationOffice.BSG);
    entityManager.persist(unit);

    var index = new DocumentationUnitIndexEntity();
    index.setDocumentationUnit(unit);
    unit.setDocumentationUnitIndex(index);
    AdmIndex admIndex = index.getAdmIndex();
    admIndex.setLangueberschrift(langueberschrift);
    admIndex.setFundstellenCombined(fundstellen);
    admIndex.setFundstellen(List.of(fundstellen));
    admIndex.setZitierdatenCombined(zitierdaten);
    admIndex.setZitierdaten(List.of(zitierdaten));
    entityManager.persistAndFlush(index);
  }

  @Test
  void findByDocumentNumber() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    Year thisYear = Year.now();
    documentationUnitEntity.setDocumentNumber(String.format("KSNR%s000002", thisYear));
    documentationUnitEntity.setJson("{\"test\":\"content\"}");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    entityManager.persistAndFlush(documentationUnitEntity);

    // when
    Optional<DocumentationUnit> documentationUnit =
      documentationUnitPersistenceService.findByDocumentNumber(
        documentationUnitEntity.getDocumentNumber()
      );

    // then
    assertThat(documentationUnit)
      .isPresent()
      .hasValueSatisfying(actual -> assertThat(actual.json()).isEqualTo("{\"test\":\"content\"}"));
  }

  @Test
  @DisplayName("Find a documentation unit with a note")
  void findByDocumentNumber_withNote() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR2025000222");
    documentationUnitEntity.setJson("{\"test\":\"content\"}");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);
    NoteEntity noteEntity = new NoteEntity();
    noteEntity.setNote("Notiz");
    noteEntity.setDocumentationUnit(documentationUnitEntity);
    entityManager.persistAndFlush(noteEntity);

    // when
    Optional<DocumentationUnit> documentationUnit =
      documentationUnitPersistenceService.findByDocumentNumber(
        documentationUnitEntity.getDocumentNumber()
      );

    // then
    assertThat(documentationUnit)
      .isPresent()
      .hasValueSatisfying(actual ->
        assertThat(actual.json()).isEqualTo("{\"test\":\"content\",\"note\":\"Notiz\"}")
      );
  }

  @Test
  @WithMockAdmUser
  void create() {
    // given

    // when
    DocumentationUnit documentationUnit = documentationUnitPersistenceService.create(
      DocumentCategory.VERWALTUNGSVORSCHRIFTEN
    );

    // then
    assertThat(
      entityManager.find(DocumentationUnitEntity.class, documentationUnit.id())
    ).isNotNull();
  }

  @Test
  void update() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR2025000001");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
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
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);
    DocumentationUnitIndexEntity documentationUnitIndexEntity = new DocumentationUnitIndexEntity();
    documentationUnitIndexEntity.setDocumentationUnit(documentationUnitEntity);
    AdmIndex admIndex = documentationUnitIndexEntity.getAdmIndex();
    admIndex.setLangueberschrift("Lang");
    admIndex.setFundstellenCombined("Fund");
    admIndex.setZitierdatenCombined("2012-12-12");
    documentationUnitIndexEntity = entityManager.persistFlushFind(documentationUnitIndexEntity);
    documentationUnitEntity.setDocumentationUnitIndex(documentationUnitIndexEntity);
    documentationUnitEntity = entityManager.merge(documentationUnitEntity);
    String json = TestFile.readFileToString("adm/json-example.json");

    // when
    documentationUnitPersistenceService.update(documentationUnitEntity.getDocumentNumber(), json);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(
        duie -> duie.getAdmIndex().getLangueberschrift(),
        duie -> duie.getAdmIndex().getFundstellenCombined(),
        duie -> duie.getAdmIndex().getZitierdatenCombined()
      )
      .containsExactly(
        "1. Bekanntmachung zum XML-Testen in NeuRIS VwV",
        "Das Periodikum 2021, Seite 15",
        "2025-05-05 2025-06-01"
      );
  }

  @Test
  @DisplayName("Updated an existing documentation unit and its note")
  void update_withNote() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR2025000111");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    entityManager.persistAndFlush(documentationUnitEntity);

    // when
    documentationUnitPersistenceService.update(
      documentationUnitEntity.getDocumentNumber(),
      "{\"test\":\"content\",\"note\":\"Neue Notiz\"}"
    );

    // then
    assertThat(
      entityManager
        .getEntityManager()
        .createQuery("from NoteEntity", NoteEntity.class)
        .getResultList()
    )
      .singleElement()
      .extracting(NoteEntity::getNote)
      .isEqualTo("Neue Notiz");
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
  @DisplayName("After publishing a documentation unit, given JSON and XML is returned.")
  void publish() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR2025000001p");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    UUID id = entityManager.persistFlushFind(documentationUnitEntity).getId();

    // when
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    String json = TestFile.readFileToString("adm/json-example.json");
    documentationUnitPersistenceService.publish(
      documentationUnitEntity.getDocumentNumber(),
      json,
      xml
    );

    // then
    assertThat(entityManager.find(DocumentationUnitEntity.class, id))
      .extracting(DocumentationUnitEntity::getJson, DocumentationUnitEntity::getXml)
      .containsExactly(json, xml);
  }

  @Test
  @DisplayName(
    "If a document with an unknown ID is published, this does not lead to creating the document."
  )
  void publish_notFound() {
    // given

    // when
    DocumentationUnit published = documentationUnitPersistenceService.publish(
      "gibtsnicht",
      "{\"test\":\"content\"",
      "<akn:akomaNtoso/>"
    );

    // then
    assertThat(published).isNull();
  }

  @Test
  void findAdmDocumentationUnitOverviewElements() {
    // given
    var documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR2025100001");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
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
    documentationUnitEntity.setDocumentationUnitIndex(documentationUnitIndexEntity);
    AdmIndex admIndex = documentationUnitIndexEntity.getAdmIndex();
    admIndex.setLangueberschrift("Sample Document Title 1");
    admIndex.setFundstellenCombined("p.abbrev.1 zitatstelle 1 p.abbrev.2 zitatstelle 2");
    admIndex.setFundstellen(List.of("p.abbrev.1 zitatstelle 1", "p.abbrev.2 zitatstelle 2"));
    admIndex.setZitierdatenCombined("2011-11-11");
    admIndex.setZitierdaten(List.of("2011-11-11"));
    entityManager.persistAndFlush(documentationUnitIndexEntity);

    // when
    var documentationUnitOverviewElements =
      documentationUnitPersistenceService.findAdmDocumentationUnitOverviewElements(
        new AdmDocumentationUnitQuery(
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
      .asInstanceOf(InstanceOfAssertFactories.list(AdmDocumentationUnitOverviewElement.class))
      .filteredOn(documentationUnitOverviewElement ->
        documentationUnitOverviewElement.documentNumber().equals("KSNR2025100001")
      )
      .singleElement()
      .extracting(
        AdmDocumentationUnitOverviewElement::zitierdaten,
        AdmDocumentationUnitOverviewElement::langueberschrift,
        AdmDocumentationUnitOverviewElement::fundstellen
      )
      .containsExactly(
        List.of("2011-11-11"),
        "Sample Document Title 1",
        List.of("p.abbrev.1 zitatstelle 1", "p.abbrev.2 zitatstelle 2")
      );
  }

  @Test
  void findAdmDocumentationUnitOverviewElements_withoutFundstellen() {
    // given
    var documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR2025100002");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
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
    AdmIndex admIndex = documentationUnitIndexEntity.getAdmIndex();
    admIndex.setLangueberschrift("Sample Document Title 1");
    admIndex.setZitierdatenCombined("2011-11-11");
    admIndex.setZitierdaten(List.of("2011-11-11"));
    documentationUnitEntity.setDocumentationUnitIndex(documentationUnitIndexEntity);
    entityManager.persistAndFlush(documentationUnitIndexEntity);

    // when
    var documentationUnitOverviewElements =
      documentationUnitPersistenceService.findAdmDocumentationUnitOverviewElements(
        new AdmDocumentationUnitQuery(
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
      .asInstanceOf(InstanceOfAssertFactories.list(AdmDocumentationUnitOverviewElement.class))
      .filteredOn(documentationUnitOverviewElement ->
        documentationUnitOverviewElement.documentNumber().equals("KSNR2025100002")
      )
      .singleElement()
      .extracting(
        AdmDocumentationUnitOverviewElement::zitierdaten,
        AdmDocumentationUnitOverviewElement::langueberschrift,
        AdmDocumentationUnitOverviewElement::fundstellen
      )
      .containsExactly(List.of("2011-11-11"), "Sample Document Title 1", null);
  }

  @Test
  void findAdmDocumentationUnitOverviewElements_byDocumentNumber() {
    // given
    createTestUnit("KSNR00001", "Title A", "Fundstelle A", "2025-01-01");
    createTestUnit("KSNR00002", "Title B", "Fundstelle B", "2025-01-02");

    // when
    var query = new AdmDocumentationUnitQuery(
      "KSNR00001",
      null,
      null,
      null,
      new QueryOptions(0, 10, "id", Sort.Direction.ASC, true)
    );
    var result = documentationUnitPersistenceService.findAdmDocumentationUnitOverviewElements(
      query
    );

    // then
    assertThat(result.content())
      .hasSize(1)
      .singleElement()
      .extracting(AdmDocumentationUnitOverviewElement::documentNumber)
      .isEqualTo("KSNR00001");
  }

  @Test
  void findAdmDocumentationUnitOverviewElements_byLangueberschrift() {
    // given
    createTestUnit("KSNR00003", "A very specific title", "Fundstelle C", "2025-01-03");
    createTestUnit("KSNR00004", "Another title", "Fundstelle D", "2025-01-04");

    // when
    var query = new AdmDocumentationUnitQuery(
      null,
      "specific",
      null,
      null,
      new QueryOptions(0, 10, "id", Sort.Direction.ASC, true)
    );
    var result = documentationUnitPersistenceService.findAdmDocumentationUnitOverviewElements(
      query
    );

    // then
    assertThat(result.content())
      .hasSize(1)
      .singleElement()
      .extracting(AdmDocumentationUnitOverviewElement::langueberschrift)
      .isEqualTo("A very specific title");
  }

  @Test
  void findAdmDocumentationUnitOverviewElements_byFundstellen() {
    // given
    createTestUnit("KSNR00005", "Title E", "Fundstelle Alpha", "2025-01-05");
    createTestUnit("KSNR00006", "Title F", "Fundstelle Beta", "2025-01-06");

    // when
    var query = new AdmDocumentationUnitQuery(
      null,
      null,
      "lph",
      null,
      new QueryOptions(0, 10, "id", Sort.Direction.ASC, true)
    );
    var result = documentationUnitPersistenceService.findAdmDocumentationUnitOverviewElements(
      query
    );

    // then
    assertThat(result.content())
      .hasSize(1)
      .singleElement()
      .extracting(AdmDocumentationUnitOverviewElement::fundstellen)
      .isEqualTo(List.of("Fundstelle Alpha"));
  }

  @Test
  void findAdmDocumentationUnitOverviewElements_byZitierdaten() {
    // given
    createTestUnit("KSNR00007", "Title G", "Fundstelle G", "2025-01-07");
    createTestUnit("KSNR00008", "Title H", "Fundstelle H", "2025-01-08");

    // when
    var query = new AdmDocumentationUnitQuery(
      null,
      null,
      null,
      "2025-01-07",
      new QueryOptions(0, 10, "id", Sort.Direction.ASC, true)
    );
    var result = documentationUnitPersistenceService.findAdmDocumentationUnitOverviewElements(
      query
    );

    // then
    assertThat(result.content())
      .hasSize(1)
      .singleElement()
      .extracting(AdmDocumentationUnitOverviewElement::zitierdaten)
      .isEqualTo(List.of("2025-01-07"));
  }

  @Test
  void findLiteratureDocumentationUnitOverviewElements_resolvesDocumentTypeAbbreviations() {
    // given
    var unit = new DocumentationUnitEntity();
    unit.setDocumentNumber("KVLS2025000999");
    unit.setDocumentationUnitType(DocumentCategory.LITERATUR_SELBSTAENDIG);
    unit.setDocumentationOffice(DocumentationOffice.BVERFG);
    unit.setXml("<test>content</test>");
    entityManager.persist(unit);

    var index = new DocumentationUnitIndexEntity();
    index.setDocumentationUnit(unit);
    LiteratureIndex literatureIndex = index.getLiteratureIndex();
    literatureIndex.setTitel("Complex Legal Analysis");
    literatureIndex.setVeroeffentlichungsjahr("2025");
    literatureIndex.setDokumenttypenCombined("Dis Bib");
    literatureIndex.setDokumenttypen(List.of("Dis", "Bib"));
    unit.setDocumentationUnitIndex(index);
    entityManager.persistAndFlush(index);

    // when
    var query = new SliDocumentationUnitQuery(
      "KVLS2025000999",
      null,
      null,
      null,
      null,
      new QueryOptions(0, 10, "id", Sort.Direction.ASC, true)
    );

    var resultPage = documentationUnitPersistenceService.findSliDocumentationUnitOverviewElements(
      query
    );

    // then
    assertThat(resultPage.content())
      .singleElement()
      .satisfies(element -> {
        assertThat(element.documentNumber()).isEqualTo("KVLS2025000999");
        assertThat(element.dokumenttypen()).containsExactlyInAnyOrder("Dis", "Bib");
      });
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
