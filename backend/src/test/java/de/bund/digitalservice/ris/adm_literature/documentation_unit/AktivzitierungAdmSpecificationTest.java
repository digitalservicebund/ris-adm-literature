package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaContextHolder;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import io.hypersistence.utils.hibernate.query.SQLExtractor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class AktivzitierungAdmSpecificationTest {

  @Autowired
  private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    // Ensuring we act as if we are in the ADM schema
    SchemaContextHolder.setSchema(SchemaType.ADM);
  }

  @AfterEach
  void tearDown() {
    SchemaContextHolder.clear();
  }

  @Test
  @DisplayName("toPredicate with no parameters should generate select with no real filters")
  void toPredicate_withNoParameters() {
    AktivzitierungAdmSpecification spec = new AktivzitierungAdmSpecification(
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    assertThat(sql).doesNotContainIgnoringCase("join");
    String whereClause = sql.substring(sql.toLowerCase().lastIndexOf("where"));
    assertThat(whereClause).isEqualToIgnoringCase("where 1=1");
  }

  @Test
  @DisplayName("toPredicate with documentNumber should add like clause but no join")
  void toPredicate_withDocumentNumberOnly() {
    AktivzitierungAdmSpecification spec = new AktivzitierungAdmSpecification(
      "DOC-1",
      null,
      null,
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    assertThat(sql)
      .contains("where")
      .contains("lower(due1_0.document_number) like ?")
      .doesNotContain("join");
  }

  @Test
  @DisplayName("toPredicate with periodikum should add like clause and left join on index")
  void toPredicate_withPeriodikumOnly() {
    AktivzitierungAdmSpecification spec = new AktivzitierungAdmSpecification(
      null,
      "BGBI",
      null,
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains("lower(dui1_0.fundstellen_combined) like ?");
  }

  @Test
  @DisplayName("toPredicate with aktenzeichen should use combined field in join")
  void toPredicate_withAktenzeichenOnly() {
    AktivzitierungAdmSpecification spec = new AktivzitierungAdmSpecification(
      null,
      null,
      null,
      null,
      "AZ-123",
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains("lower(dui1_0.aktenzeichen_list_combined) like ?");
  }

  @Test
  @DisplayName("toPredicate with normgeber should use combined field in join")
  void toPredicate_withNormgeberOnly() {
    AktivzitierungAdmSpecification spec = new AktivzitierungAdmSpecification(
      null,
      null,
      null,
      null,
      null,
      null,
      "BMJ"
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains("lower(dui1_0.normgeber_list_combined) like ?");
  }

  @Test
  @DisplayName("toPredicate with all parameters should combine all clauses correctly")
  void toPredicate_withAllParameters() {
    AktivzitierungAdmSpecification spec = new AktivzitierungAdmSpecification(
      "DOC-1",
      "BGBI",
      "S5",
      "2024",
      "AZ-1",
      "Type-A",
      "BMJ"
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains("lower(due1_0.document_number) like ?")
      .contains("and lower(dui1_0.fundstellen_combined) like ?")
      .contains("and lower(dui1_0.inkrafttretedatum) like ?")
      .contains("and lower(dui1_0.aktenzeichen_list_combined) like ?")
      .contains("and lower(dui1_0.dokumenttyp) like ?")
      .contains("and lower(dui1_0.normgeber_list_combined) like ?");
  }
}
