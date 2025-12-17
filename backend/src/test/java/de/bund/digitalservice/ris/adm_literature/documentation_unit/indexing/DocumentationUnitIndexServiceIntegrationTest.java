package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import de.bund.digitalservice.ris.adm_literature.test.TestFile;
import jakarta.persistence.TypedQuery;
import java.time.Year;
import org.junit.jupiter.api.Disabled;
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
class DocumentationUnitIndexServiceIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private DocumentationUnitIndexService documentationUnitIndexService;

  @Test
  void indexByAdmDocumentationUnit_xml() {
    // given
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR9999999999");
    documentationUnitEntity.setXml(xml);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.ADM);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getAdmIndex)
      .extracting(
        AdmIndex::getLangueberschrift,
        AdmIndex::getFundstellenCombined,
        AdmIndex::getZitierdatenCombined
      )
      .containsExactly(
        "1. Bekanntmachung zum XML-Testen in NeuRIS VwV",
        "Das Periodikum 2021, Seite 15",
        "2025-05-05 2025-06-01"
      );
  }

  @Test
  void indexByAdmDocumentationUnit_json() {
    // given
    String json = TestFile.readFileToString("adm/json-example.json");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR777777777");
    documentationUnitEntity.setJson(json);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.ADM);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getAdmIndex)
      .extracting(
        AdmIndex::getLangueberschrift,
        AdmIndex::getFundstellenCombined,
        AdmIndex::getZitierdatenCombined
      )
      .containsExactly(
        "1. Bekanntmachung zum XML-Testen in NeuRIS VwV",
        "Das Periodikum 2021, Seite 15",
        "2025-05-05 2025-06-01"
      );
  }

  @Test
  void indexByAdmDocumentationUnit_jsonNotValid() {
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
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.ADM);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getAdmIndex)
      .extracting(
        AdmIndex::getLangueberschrift,
        AdmIndex::getFundstellenCombined,
        AdmIndex::getZitierdatenCombined
      )
      .containsExactly(null, null, null);
  }

  @Test
  void indexByAdmDocumentationUnit_jsonAndXml() {
    // given
    String json = TestFile.readFileToString("adm/json-example.json");
    String xml = TestFile.readFileToString("adm/ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR555555555");
    documentationUnitEntity.setJson(json);
    documentationUnitEntity.setXml(xml);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.ADM);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getAdmIndex)
      .extracting(
        AdmIndex::getLangueberschrift,
        AdmIndex::getFundstellenCombined,
        AdmIndex::getZitierdatenCombined
      )
      .containsExactly(
        "1. Bekanntmachung zum XML-Testen in NeuRIS VwV",
        "Das Periodikum 2021, Seite 15",
        "2025-05-05 2025-06-01"
      );
  }

  @Test
  void indexByAdmDocumentationUnit_jsonAndXmlAreNull() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR333333333");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.ADM);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getAdmIndex)
      .extracting(
        AdmIndex::getLangueberschrift,
        AdmIndex::getFundstellenCombined,
        AdmIndex::getZitierdatenCombined
      )
      .containsExactly(null, null, null);
  }

  @Test
  @Disabled("Needs to be implemented with conversion from LDML to Json")
  void indexBySliDocumentationUnit_xml() {
    // given
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KVLS9999999999");
    documentationUnitEntity.setXml(xml);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_SELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BVERFG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly(
        "Hauptsache Titel - eine Gesetzgebungsgutschrift",
        "2022ff",
        "Ebs Gut",
        null
      );
  }

  @Test
  void indexBySliDocumentationUnit_json() {
    // given
    String json = TestFile.readFileToString("literature/sli/json-example.json");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KVLS2025000009");
    documentationUnitEntity.setJson(json);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_SELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BVERFG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly(
        "Hauptsache Titel - eine Gesetzgebungsgutschrift",
        "2022ff",
        "Ebs Gut",
        null
      );
  }

  @Test
  void indexBySliDocumentationUnit_jsonNotValid() {
    // given
    var documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KVLS000004711");
    documentationUnitEntity.setJson(
      """
      {
        "id": "11111111-1657-4085-ae2a-993a04c27f6b",
        "documentNumber": "KVLS000004711",
        [] ooops
      }
      """
    );
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_SELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BVERFG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly(null, null, null, null);
  }

  @Test
  @Disabled("Needs to be implemented with conversion from LDML to Json")
  void indexBySliDocumentationUnit_jsonAndXml() {
    // given
    String json = TestFile.readFileToString("literature/sli/json-example.json");
    String xml = TestFile.readFileToString("literature/sli/ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KVLS555555555");
    documentationUnitEntity.setJson(json);
    documentationUnitEntity.setXml(xml);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly("", "", "Ebs Kon", null);
  }

  @Test
  void indexBySliDocumentationUnit_jsonAndXmlAreNull() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KVLS333333333");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_SELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BVERFG);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly(null, null, null, null);
  }

  @Test
  @Disabled("Needs to be implemented with conversion from LDML to Json")
  void indexByUliDocumentationUnit_xml() {
    // given
    String xml = TestFile.readFileToString("literature/uli/ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("STLU9999999999");
    documentationUnitEntity.setXml(xml);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BFH);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly(
        "Hauptsache Titel - eine Gesetzgebungsgutschrift",
        "2022ff",
        "Ebs Gut",
        null
      );
  }

  @Test
  void indexByUliDocumentationUnit_json() {
    // given
    String json = TestFile.readFileToString("literature/sli/json-example.json");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("STLU2025000009");
    documentationUnitEntity.setJson(json);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BFH);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly(
        "Hauptsache Titel - eine Gesetzgebungsgutschrift",
        "2022ff",
        "Ebs Gut",
        null
      );
  }

  @Test
  void indexByUliDocumentationUnit_jsonNotValid() {
    // given
    var documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("STLU000004711");
    documentationUnitEntity.setJson(
      """
      {
        "id": "11111111-1657-4085-ae2a-993a04c27f6b",
        "documentNumber": "STLU000004711",
        [] ooops
      }
      """
    );
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BFH);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly(null, null, null, null);
  }

  @Test
  @Disabled("Needs to be implemented with conversion from LDML to Json")
  void indexByUliDocumentationUnit_jsonAndXml() {
    // given
    String json = TestFile.readFileToString("literature/uli/json-example.json");
    String xml = TestFile.readFileToString("literature/uli/ldml-example.akn.xml");
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("STLU555555555");
    documentationUnitEntity.setJson(json);
    documentationUnitEntity.setXml(xml);
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BFH);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly("", "", "Ebs Kon", null);
  }

  @Test
  void indexByUliDocumentationUnit_jsonAndXmlAreNull() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("STLU333333333");
    documentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_UNSELBSTAENDIG);
    documentationUnitEntity.setDocumentationOffice(DocumentationOffice.BFH);
    documentationUnitEntity = entityManager.persistFlushFind(documentationUnitEntity);

    // when
    documentationUnitIndexService.updateIndex(SchemaType.LIT);

    // then
    TypedQuery<DocumentationUnitIndexEntity> query = createTypedQuery(documentationUnitEntity);
    assertThat(query.getResultList())
      .singleElement()
      .extracting(DocumentationUnitIndexEntity::getLiteratureIndex)
      .extracting(
        LiteratureIndex::getTitel,
        LiteratureIndex::getVeroeffentlichungsjahr,
        LiteratureIndex::getDokumenttypenCombined,
        LiteratureIndex::getVerfasserListCombined
      )
      .containsExactly(null, null, null, null);
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
